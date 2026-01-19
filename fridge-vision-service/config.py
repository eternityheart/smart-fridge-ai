"""
智能冰箱视觉服务 - 配置管理模块
支持抽屉式环境切换 (dev/prod/docker)
"""

import os
from pathlib import Path
from dotenv import load_dotenv

class Config:
    """配置基类"""
    
    # 服务配置
    SERVICE_HOST = os.getenv('SERVICE_HOST', '0.0.0.0')
    SERVICE_PORT = int(os.getenv('SERVICE_PORT', 5000))
    
    # 模型配置
    MODEL_PATH = os.getenv('MODEL_PATH', 'models/yolov8n.pt')
    CONFIDENCE_THRESHOLD = float(os.getenv('CONFIDENCE_THRESHOLD', 0.5))
    IOU_THRESHOLD = float(os.getenv('IOU_THRESHOLD', 0.45))
    
    # GPU 配置
    USE_GPU = os.getenv('USE_GPU', 'True').lower() == 'true'
    
    # 日志配置
    LOG_LEVEL = os.getenv('LOG_LEVEL', 'INFO')
    LOG_FILE = os.getenv('LOG_FILE', 'logs/vision.log')
    
    # 后端服务
    BACKEND_URL = os.getenv('BACKEND_URL', 'http://localhost:8080')
    
    # DeepSeek API
    DEEPSEEK_API_KEY = os.getenv('DEEPSEEK_API_KEY', '')
    DEEPSEEK_BASE_URL = os.getenv('DEEPSEEK_BASE_URL', 'https://api.deepseek.com/v1')


class DevelopmentConfig(Config):
    """开发环境配置"""
    DEBUG = True
    LOG_LEVEL = 'DEBUG'


class ProductionConfig(Config):
    """生产环境配置"""
    DEBUG = False
    LOG_LEVEL = 'INFO'
    CONFIDENCE_THRESHOLD = 0.6


class DockerConfig(Config):
    """Docker 环境配置"""
    DEBUG = False
    BACKEND_URL = 'http://fridge-api:8080'


# 环境配置映射
config_map = {
    'dev': DevelopmentConfig,
    'development': DevelopmentConfig,
    'prod': ProductionConfig,
    'production': ProductionConfig,
    'docker': DockerConfig,
}


def load_config(env: str = None) -> Config:
    """
    加载配置
    
    抽屉式环境管理：
    1. 首先尝试加载对应环境的 .env 文件
    2. 然后返回对应的配置类
    
    Args:
        env: 环境名称 (dev/prod/docker)，默认从 FLASK_ENV 读取
    
    Returns:
        配置对象
    """
    # 获取环境名称
    if env is None:
        env = os.getenv('FLASK_ENV', 'dev')
    
    # 加载对应的 .env 文件
    base_path = Path(__file__).parent
    env_file = base_path / f'.env.{env}'
    
    if env_file.exists():
        load_dotenv(env_file)
        print(f"[配置] 已加载环境文件: {env_file}")
    else:
        print(f"[配置] 环境文件不存在: {env_file}，使用默认配置")
    
    # 获取配置类
    config_class = config_map.get(env, DevelopmentConfig)
    print(f"[配置] 当前环境: {env}, 配置类: {config_class.__name__}")
    
    return config_class()


# 导出当前配置
current_config = load_config()
