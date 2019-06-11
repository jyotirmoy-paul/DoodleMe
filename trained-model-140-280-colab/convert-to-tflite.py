import tensorflow as tf

converter = tf.lite.TFLiteConverter.from_keras_model_file('model-280-v3-6348.h5')
tflite_model = converter.convert()
open('cnn-model.tflite', 'wb').write(tflite_model)
