[![Image.sc forum](https://img.shields.io/badge/dynamic/json.svg?label=forum&url=https%3A%2F%2Fforum.image.sc%2Ftags%2Fgiani.json&query=%24.topic_list.tags.0.topic_count&colorB=brightgreen&&suffix=%20topics&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAABPklEQVR42m3SyyqFURTA8Y2BER0TDyExZ+aSPIKUlPIITFzKeQWXwhBlQrmFgUzMMFLKZeguBu5y+//17dP3nc5vuPdee6299gohUYYaDGOyyACq4JmQVoFujOMR77hNfOAGM+hBOQqB9TjHD36xhAa04RCuuXeKOvwHVWIKL9jCK2bRiV284QgL8MwEjAneeo9VNOEaBhzALGtoRy02cIcWhE34jj5YxgW+E5Z4iTPkMYpPLCNY3hdOYEfNbKYdmNngZ1jyEzw7h7AIb3fRTQ95OAZ6yQpGYHMMtOTgouktYwxuXsHgWLLl+4x++Kx1FJrjLTagA77bTPvYgw1rRqY56e+w7GNYsqX6JfPwi7aR+Y5SA+BXtKIRfkfJAYgj14tpOF6+I46c4/cAM3UhM3JxyKsxiOIhH0IO6SH/A1Kb1WBeUjbkAAAAAElFTkSuQmCC)](https://forum.image.sc/tags/giani) [![Build](https://github.com/djpbarry/Giani/actions/workflows/maven.yml/badge.svg)](https://github.com/djpbarry/Giani/actions/workflows/maven.yml) ![Commit activity](https://img.shields.io/github/commit-activity/y/djpbarry/giani?style=plastic) ![GitHub](https://img.shields.io/github/license/djpbarry/giani?color=green&style=plastic) ![GIANI Icon](assets/GianiIcon.png)

# Overview

GIANI is a FIJI plugin designed to facilitate routine analysis of 3D cell biology image data. Implemented specifically with batch-processing in mind, GIANI is designed to:

* Read data sets in their native, proprietary format
* Automatically segment nuclei and cells
* Measure nuclear and cell morphology, together with fluorescence intensities within these regions.
* Allow users to explore their results

# Segment Nuclei and Cells

Nuclei locations are estimated using blob detection...

![GIANI Detect Nuclei Centres Preview](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Detect_Nuclei_Centres_Preview.PNG)

...before full segmentation is achieved using a watershed operation...

![Nuclear Segmentation Preview](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Segment_Nuclei_Preview.PNG)


...and then cells are similarly segmented:

![Cell Segmentation Preview](
https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Segment_Cells_Preview.PNG)

# Obtain Measurements

A complete set of morphological and intensity descriptors, across an unlimited number of channels, can then be obtained from the above segmentations.

![GIANI Measurement Preview](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Measurement_Preview.PNG)

# Further Segmentation

If you wish, you can segment smaller, sub-cellular structures using additional blob detection:

![GIANI Detect Spots Preview](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Detect_Spots_Preview.PNG)

# Explore Results

Examine your results using the included results browser:

![GIANI Results Browser Post Directory Selection](
https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Results_Browser_Final.PNG)

# Want To Learn More?

See [the Wiki](https://github.com/djpbarry/Giani/wiki) for installation instructions and full documentation.


# Citing GIANI

Please cite the following publication if you use GIANI in your work:

David J. Barry, Claudia Gerri, Donald M. Bell, Rocco D'Antuono, Kathy K. Niakan; GIANI – open-source software for automated analysis of 3D microscopy images. J Cell Sci 15 May 2022; 135 (10): jcs259511. doi: https://doi.org/10.1242/jcs.259511
