"""
智能冰箱菜谱系统 - Python 视觉服务
基于 Flask + YOLOv8 的食材识别服务

抽屉式环境配置使用方法：
- 开发环境: set FLASK_ENV=dev && python app.py
- 生产环境: set FLASK_ENV=prod && python app.py
- Docker:   设置环境变量 FLASK_ENV=docker
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import os
import cv2
import numpy as np
from datetime import datetime

# 加载抽屉式配置
from config import load_config, Config

# 获取当前环境配置
config: Config = load_config()

# 尝试导入 ultralytics
try:
    from ultralytics import YOLO
    YOLO_AVAILABLE = True
except ImportError:
    YOLO_AVAILABLE = False
    print("警告: ultralytics 未安装，使用模拟模式")

app = Flask(__name__)
CORS(app)

# 模型路径
MODEL_PATH = config.MODEL_PATH

# 加载模型
model = None
if YOLO_AVAILABLE and os.path.exists(MODEL_PATH):
    model = YOLO(MODEL_PATH)
    print(f"[模型] 加载成功: {MODEL_PATH}")
elif YOLO_AVAILABLE:
    # 尝试下载默认模型
    try:
        model = YOLO('yolov8n.pt')
        print("[模型] 使用默认 YOLOv8n 模型")
    except Exception as e:
        print(f"[模型] 无法加载模型: {e}")


@app.route('/health', methods=['GET'])
def health():
    """健康检查接口"""
    return jsonify({
        'status': 'UP',
        'service': 'fridge-vision-service',
        'environment': os.getenv('FLASK_ENV', 'dev'),
        'yolo_available': YOLO_AVAILABLE,
        'model_loaded': model is not None,
        'gpu_enabled': config.USE_GPU,
        'timestamp': datetime.now().isoformat()
    })


@app.route('/detect', methods=['POST'])
def detect():
    """
    图像识别接口
    接收图片，返回识别结果
    """
    if 'image' not in request.files:
        return jsonify({'error': '缺少图片参数'}), 400
    
    file = request.files['image']
    if file.filename == '':
        return jsonify({'error': '文件名为空'}), 400
    
    try:
        # 读取图片
        img_bytes = file.read()
        img_array = np.frombuffer(img_bytes, np.uint8)
        img = cv2.imdecode(img_array, cv2.IMREAD_COLOR)
        
        if img is None:
            return jsonify({'error': '无法解析图片'}), 400
        
        # 如果模型可用，进行检测
        if model is not None:
            results = model(img, conf=config.CONFIDENCE_THRESHOLD, iou=config.IOU_THRESHOLD)
            
            detections = []
            for r in results:
                for box in r.boxes:
                    detections.append({
                        'label': model.names[int(box.cls)],
                        'confidence': round(float(box.conf), 3),
                        'bbox': [round(x, 2) for x in box.xyxy[0].tolist()]
                    })
            
            return jsonify({
                'success': True,
                'detections': detections,
                'count': len(detections),
                'config': {
                    'confidence_threshold': config.CONFIDENCE_THRESHOLD,
                    'iou_threshold': config.IOU_THRESHOLD
                }
            })
        else:
            # 模拟模式
            return jsonify({
                'success': True,
                'mode': 'simulation',
                'detections': [
                    {'label': '鸡蛋', 'confidence': 0.95, 'bbox': [100, 100, 200, 200]},
                    {'label': '牛奶', 'confidence': 0.88, 'bbox': [300, 100, 400, 300]},
                    {'label': '西红柿', 'confidence': 0.82, 'bbox': [150, 300, 250, 400]}
                ],
                'count': 3,
                'message': '模拟模式，请安装 ultralytics 并下载模型'
            })
            
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/config', methods=['GET'])
def get_config():
    """获取当前配置信息"""
    return jsonify({
        'environment': os.getenv('FLASK_ENV', 'dev'),
        'service_host': config.SERVICE_HOST,
        'service_port': config.SERVICE_PORT,
        'model_path': config.MODEL_PATH,
        'confidence_threshold': config.CONFIDENCE_THRESHOLD,
        'iou_threshold': config.IOU_THRESHOLD,
        'use_gpu': config.USE_GPU,
        'backend_url': config.BACKEND_URL,
        'log_level': config.LOG_LEVEL
    })


if __name__ == '__main__':
    print("====================================")
    print("  冰箱视觉识别服务启动中...")
    print(f"  环境: {os.getenv('FLASK_ENV', 'dev')}")
    print(f"  地址: http://{config.SERVICE_HOST}:{config.SERVICE_PORT}")
    print(f"  健康检查: http://localhost:{config.SERVICE_PORT}/health")
    print(f"  GPU 加速: {'启用' if config.USE_GPU else '禁用'}")
    print("====================================")
    
    app.run(
        host=config.SERVICE_HOST, 
        port=config.SERVICE_PORT, 
        debug=getattr(config, 'DEBUG', False)
    )
