<img src="https://github.com/jyotirmoy-paul/DoodleMe/blob/master/drawable-assets/icon.png" width="150" />

# DoodleMe
"Doodle Me" project is CNN (Convolutional Neural Network) based deep learning model which is trained to distinguish 88 different doodle categories.

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
> For correctly setting up the android studio project, refer [README.md]()

## Features
- Neural Network based on CNN architecture
- Trained on over 0.44 million unique doodle drawing
- Tested against 88 thousands unique doodles, achieving an accuracy of 67.59%
- Robust android app included for testing and model

## CNN Architecture

<img src="https://github.com/jyotirmoy-paul/DoodleMe/blob/master/drawable-assets/cnn-architecture.png"/>

> To get details of the CNN Architecture, check out the [Jupyter Notebook - CNN Architecture](https://github.com/jyotirmoy-paul/DoodleMe/blob/master/cnn_model_v5_28.ipynb)

- Image preprocessing before feeding into the neural net - this is done to keep only the important black colored pixel and removing other noises around it
```
def preprocessing(img):
    img = img/255
    return np.where(img < 0.2, 0, 1)
```
- The processed image is passed through two combinations of Convolution Layer and a Max Pooling layer for extracting important features from the image
- The output from the convolution layer is flattened and feeded into the fully connected layer of 128 units
- And finally, the output layer consists of 88 units, with the activation function `softmax`. The model outputs 88 probabilities corresponding to each of the 88 categories

## Attribution

The Quick, Draw! Dataset has a collection of 50 Million drawings across 345 categories.
This data is made available by Google, Inc. under the Creative Commons Attribution 4.0 International license.
Google Creative Cloud: [Quick, Draw! Dataset](https://github.com/googlecreativelab/quickdraw-dataset)


## License
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat-square)](http://badges.mit-license.org)
- **[MIT license](http://opensource.org/licenses/mit-license.php)**
- Copyright (c) 2019 [Jyotirmoy Paul](https://github.com/jyotirmoy-paul)
