"""
智能冰箱菜谱系统 - Python 视觉服务
基于 Flask + YOLOv8 的食材识别服务
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import os
import cv2
import numpy as np
from datetime import datetime

# 尝试导入 ultralytics，如果失败则使用模拟模式
try:
    from ultralytics import YOLO
    YOLO_AVAILABLE = True
except ImportError:
    YOLO_AVAILABLE = False
    print("警告: ultralytics 未安装，使用模拟模式")

app = Flask(__name__)
CORS(app)  # 允许跨域

# 模型路径
MODEL_PATH = os.path.join(os.path.dirname(__file__), 'models', 'yolov8n.pt')

# 加载模型（如果可用）
model = None
if YOLO_AVAILABLE and os.path.exists(MODEL_PATH):
    model = YOLO(MODEL_PATH)
    print(f"模型加载成功: {MODEL_PATH}")


@app.route('/health', methods=['GET'])
def health():
    """健康检查接口"""
    return jsonify({
        'status': 'UP',
        'service': 'fridge-vision-service',
        'yolo_available': YOLO_AVAILABLE,
        'model_loaded': model is not None,
        'timestamp': datetime.now().isoformat()
    })


@app.route('/detect', methods=['POST'])
def detect():
    """
    图像识别接口
    接收图片，返回识别结果
    """
    # 检查是否有文件上传
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
            results = model(img)
            
            detections = []
            for r in results:
                for box in r.boxes:
                    detections.append({
                        'label': model.names[int(box.cls)],
                        'confidence': float(box.conf),
                        'bbox': box.xyxy[0].tolist()
                    })
            
            return jsonify({
                'success': True,
                'detections': detections,
                'count': len(detections)
            })
        else:
            # 模拟模式：返回示例数据
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


@app.route('/detect/batch', methods=['POST'])
def detect_batch():
    """
    批量识别接口
    支持同时上传多张图片
    """
    if 'images' not in request.files:
        return jsonify({'error': '缺少图片参数'}), 400
    
    files = request.files.getlist('images')
    results = []
    
    for file in files:
        # TODO: 实现批量处理
        results.append({
            'filename': file.filename,
            'detections': []
        })
    
    return jsonify({
        'success': True,
        'results': results,
        'count': len(results)
    })


if __name__ == '__main__':
    print("====================================")
    print("  冰箱视觉识别服务启动中...")
    print("  API 端口: http://localhost:5000")
    print("  健康检查: http://localhost:5000/health")
    print("====================================")
    app.run(host='0.0.0.0', port=5000, debug=True)
