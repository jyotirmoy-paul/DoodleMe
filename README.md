<img src="https://github.com/jyotirmoy-paul/DoodleMe/blob/master/drawable-assets/icon.png" width="150" />

# DoodleMe
"Doodle Me" project is CNN (Convolutional Neural Network) based deep learning model which is trained to distinguish 123 different doodle categories.

## Android App live on Play Store
The final android app can be found on Play Store: [Doodle Me](https://play.google.com/store/apps/details?id=paul.cipherresfeber.doodleme)

## Table of Contents
- [Getting Started](#getting-started)
- [Features](#features)
- [CNN Architecture](#cnn-architecture)
- [Attribution](#attribution)
- [License](#license)

## Getting Started
The following instuctions will get you a copy of the project for development and testing purposes.

### Clone
- Clone this project to your local machine using `https://github.com/jyotirmoy-paul/doodleme`

### Setup
- Installation of packages or dependencies
> Install or update `keras` and `matplotlib`
```shell
$ pip3 install keras
$ pip3 install matplotlib
```
> If you wish, you can use `jupyter notebook`
```shell
$ sudo apt-get install jupyter
```
> For correctly setting up the android studio project, refer [README.md](https://github.com/jyotirmoy-paul/DoodleMe/blob/master/DoodleMeApp/README.md)

## Features
- Neural Network based on CNN architecture
- Trained on over 0.605 million unique doodle drawing
- Tested against 0.132 million unique doodles, achieving an accuracy of 68.29%
- Robust android app included for testing and model evaluation
- Sample doodles predicted by the model
<img src="https://github.com/jyotirmoy-paul/DoodleMe/blob/master/drawable-assets/doodles.png"/>

## CNN Architecture

<img src="https://github.com/jyotirmoy-paul/DoodleMe/blob/master/drawable-assets/cnn-architecture.png"/>

> To get details of the CNN Architecture, check out the [Jupyter Notebook - CNN Architecture](https://github.com/jyotirmoy-paul/DoodleMe/blob/master/cnn_model_v6_28_123.ipynb)

- Image preprocessing before feeding into the neural net - this is done to keep only the important black colored pixel and removing other noises around it
```
def preprocessing(img):
    img = img/255
    return np.where(img < 0.3, 0, 1)
```
- The processed image is passed through two combinations of Convolution Layer and a Max Pooling layer for extracting important features from the image
- The output from the convolution layer is flattened and feeded into the fully connected layer of 256 units, a dropout rate of 50% is used
- And finally, the output layer consists of 123 units, with the activation function `softmax`. The model outputs 123 probabilities corresponding to each of the 123 categories

## Attribution

The Quick, Draw! Dataset has a collection of 50 Million drawings across 345 categories.
This data is made available by Google, Inc. under the Creative Commons Attribution 4.0 International license.
Google Creative Cloud: [Quick, Draw! Dataset](https://github.com/googlecreativelab/quickdraw-dataset)


## License
```
MIT License

Copyright (c) 2019 Jyotirmoy Paul

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Contributing to Doodle Me
All pull requests are welcome

