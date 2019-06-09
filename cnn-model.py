from keras.models import Sequential
from keras.layers import Convolution2D
from keras.layers import MaxPooling2D
from keras.layers import Flatten

from keras.layers import Dense
from keras.layers import Dropout

classifier = Sequential()

# first conv layer
classifier.add(Convolution2D(
        input_shape=(28,28,1),
        filters=32,
        kernel_size=(3,3),
        activation='relu',
        kernel_initializer='uniform'))

# second conv layer
classifier.add(Convolution2D(
        filters=32,
        kernel_size=(1,1),
        activation='relu',
        kernel_initializer='uniform'))

# max pooling layer
classifier.add(MaxPooling2D(
        pool_size=(2,2),
        strides=(2,2)))

# third conv layer
classifier.add(Convolution2D(
        filters=64,
        kernel_size=(3,3),
        activation='relu',
        kernel_initializer='uniform'))

# max pooling layer
classifier.add(MaxPooling2D(
        pool_size=(2,2),
        strides=(2,2)))

# flattening for feeding the data to a fully connected artificial neural network
classifier.add(Flatten())

# first hidden layer
classifier.add(Dense(units=256, activation='relu', kernel_initializer='uniform'))
classifier.add(Dropout(rate=0.5))

# output layer
classifier.add(Dense(units=88, activation='softmax', kernel_initializer='uniform'))

# compiling the CNN
classifier.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])


# image preprocessing before feeding into the CNN
from keras.preprocessing.image import ImageDataGenerator

data_generator = ImageDataGenerator(rescale=1./255, rotation_range=0.05, validation_split=0.1)

# training data set
train_set = data_generator.flow_from_directory(
        'dataset',
        batch_size=64,
        target_size=(28,28), color_mode='grayscale', subset='training')

# testing/validation data set
test_set = data_generator.flow_from_directory(
        'dataset',
        batch_size=64,
        target_size=(28,28), color_mode='grayscale', subset='validation')

# finally fitting the data and trining the CNN
history = classifier.fit_generator(
        epochs=10,
        initial_epoch=0,
        generator=train_set,
        steps_per_epoch=1362,
        validation_data=test_set,
        validation_steps=152,
        use_multiprocessing=True)

# saving the classifier model
classifier.save('model.h5')
















