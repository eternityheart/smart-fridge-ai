---
description: AI 视觉服务开发最佳实践 - YOLOv8 + Flask + 数据增强
---

# AI Vision Service Development Skill

本 Skill 指导如何开发高准确率的 AI 视觉识别服务。

---

## 环境要求

- Python 3.10+
- PyTorch 2.0+ (GPU 版本)
- CUDA 11.8+
- GPU: RTX 2060 或更高

---

## 项目结构

```
vision-service/
├── app.py                    # Flask 主服务
├── requirements.txt
├── models/
│   └── best.pt               # 训练后的模型
├── training/
│   ├── train.py              # 训练脚本
│   ├── evaluate.py           # 评估脚本
│   ├── augmentation.py       # 数据增强
│   └── fusion_service.py     # 多信号融合
├── data/
│   ├── dataset.yaml          # 数据集配置
│   ├── train/                # 训练图片
│   └── val/                  # 验证图片
└── tests/
```

---

## Flask 服务模板

```python
from flask import Flask, request, jsonify
from ultralytics import YOLO
import cv2
import numpy as np

app = Flask(__name__)
model = YOLO('models/best.pt')

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'UP'})

@app.route('/detect', methods=['POST'])
def detect():
    if 'image' not in request.files:
        return jsonify({'error': '缺少图片'}), 400
    
    file = request.files['image']
    img_bytes = file.read()
    img = cv2.imdecode(np.frombuffer(img_bytes, np.uint8), cv2.IMREAD_COLOR)
    
    results = model(img)
    
    detections = []
    for r in results:
        for box in r.boxes:
            detections.append({
                'label': model.names[int(box.cls)],
                'confidence': float(box.conf),
                'bbox': box.xyxy[0].tolist()
            })
    
    return jsonify({'detections': detections})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
```

---

## 数据集配置 (dataset.yaml)

```yaml
path: ./data
train: train/images
val: val/images

names:
  0: egg
  1: milk
  2: carrot
  3: apple
  4: tomato
  # ...更多类别
```

---

## 训练脚本

```python
from ultralytics import YOLO

# 使用预训练模型
model = YOLO('yolov8m.pt')

# 开始训练
model.train(
    data='data/dataset.yaml',
    epochs=150,
    imgsz=640,
    batch=16,
    lr0=0.01,
    augment=True,
    mosaic=1.0,
    freeze=10,        # 冻结前10层
    patience=25,      # 早停
)

# 验证
metrics = model.val()
print(f"mAP@0.5: {metrics.box.map50:.2%}")
```

---

## 数据增强配置

```python
from albumentations import Compose, RandomBrightnessContrast, GaussianBlur, CLAHE

augmentation = Compose([
    RandomBrightnessContrast(brightness_limit=0.3, contrast_limit=0.3, p=0.8),
    GaussianBlur(blur_limit=3, p=0.3),
    CLAHE(clip_limit=2.0, p=0.3),
])
```

---

## 多信号融合

```python
def fuse_results(yolo_results, ocr_text, barcode):
    final = []
    for item in yolo_results:
        if item['confidence'] >= 0.7:
            final.append(item)
        elif item['confidence'] >= 0.4:
            # 尝试 OCR/条码验证
            if validate_with_ocr(item['label'], ocr_text):
                item['confidence'] += 0.2
                final.append(item)
    return final
```

---

## 评估指标

| 指标 | 目标 |
|-----|------|
| mAP@0.5 | ≥ 70% |
| Precision | ≥ 75% |
| Recall | ≥ 65% |
| Top-5 准确率 | ≥ 85% |

---

## 数据集来源

1. **Kaggle**: Refrigerator Contents
2. **Roboflow**: aicook dataset
3. **UEC FOOD 100**: 日本电通大学
4. **Food Recognition 2022**: Kaggle

---

## 常见问题

| 问题 | 解决方案 |
|-----|---------|
| CUDA out of memory | 减小 batch size |
| 准确率低 | 增加数据量 / 数据增强 |
| 推理慢 | 使用 TensorRT 加速 |
| 模型下载慢 | 使用镜像源 |
