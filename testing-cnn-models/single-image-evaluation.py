from keras.preprocessing import image

INPUT_IMAGE_SIZE = 140

img = image.load_img('sample_image.png', target_size=(INPUT_IMAGE_SIZE,INPUT_IMAGE_SIZE), color_mode='grayscale')
img = image.img_to_array(img)
for i in range(INPUT_IMAGE_SIZE):
    for j in range(INPUT_IMAGE_SIZE):
        if img[i][j] == 255:
            img[i][j] = 1
        else:
            img[i][j] = 0
# img = img/255
from keras.models import load_model
classifier = load_model('model-140-v2-7200.h5')
import matplotlib.pyplot as plt
plt.imshow(img.reshape(INPUT_IMAGE_SIZE,INPUT_IMAGE_SIZE), cmap='gray')
plt.show()
pred = classifier.predict_classes(img.reshape(1,INPUT_IMAGE_SIZE,INPUT_IMAGE_SIZE,1))
print(pred)
