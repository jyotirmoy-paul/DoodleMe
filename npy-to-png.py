"""
'npy-to-png.py' converts randomly selected 1000 (28,28) numpy array to png image file
and categorises it according to the npy file name
"""
import numpy as np
from matplotlib import image
from random import sample
import os

dir__ = 'raw-dataset'

def extract_n_image(filename, n=1000):
    """Function that randomly selects n (default=1000) numpy array to png formatted images,
    and saves in the 'filename' named category folder
    """
    print('Pulling ' + str(n) + ' images from ' + filename + '...', end=' ')
    img_array = np.load(dir__ + '/' + filename)
    imgs_available = img_array.shape[0]
    if not os.path.exists('dataset/'+filename[:-4]):
        os.mkdir('dataset/'+filename[:-4])
    for i in sample(range(0, imgs_available), n):
        image.imsave('dataset/'+filename[:-4]+'/'+str(i)+'.png', img_array[i].reshape(28,28), cmap='Greys', format='png')
    print('Done')

if not os.path.exists('dataset'):
    os.mkdir('dataset')


















# end
