import tensorflow as tf

converter = tf.lite.TFLiteConverter.from_keras_model_file('model-140-v2-7200.h5')
tflite_model = converter.convert()
open('cnn-model.tflite', 'wb').write(tflite_model)
