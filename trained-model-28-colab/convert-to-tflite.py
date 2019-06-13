import tensorflow as tf
import sys

<<<<<<< HEAD:trained-model-28-colab/convert-to-tflite.py
converter = tf.lite.TFLiteConverter.from_keras_model_file(sys.argv[1])
=======
converter = tf.lite.TFLiteConverter.from_keras_model_file('model-140-v2-7200.h5')
>>>>>>> 744984a2f8e000e28a8eb651c509269eaee3233d:trained-model-140-280-colab/convert-to-tflite.py
tflite_model = converter.convert()
open('cnn-model.tflite', 'wb').write(tflite_model)
