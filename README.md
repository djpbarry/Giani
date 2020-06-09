![GIANI Icon](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GianiIcon.png)

## Introduction

GIANI is a FIJI plugin designed to facilitate routine analysis of 3D cell biology image data. Implemented specifically with batch-processing in mind, GIANI is designed to:

* Read data sets in their native, proprietary
* Segment nuclei & cells
* Measure nuclear and cell morphology, together with fluorescence intensities within these regions.
* Explore results

## Segment Nuclei & Cells

Nuclei locations are estimated using blob detection...

![GIANI Detect Nuclei Centres Preview](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Detect_Nuclei_Centres_Preview.PNG)

...before full segmentation is achieved using a watershed operation...

![Nuclear Segmentation Preview](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Segment_Nuclei_Preview.PNG)


...and then cells are similarly segmented:

![Cell Segmentation Preview](
https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Segment_Cells_Preview.PNG)

## Obtain Measurements

A complete set of morpholical and intesnity descriptiors, across an unlimited number of channels, can then be obtained from the above segmentations.

![GIANI Measurement Preview](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Measurement_Preview.PNG)

## Further Segmentation

If you wish, you can segment smaller, sub-cellular structures using additional blob detection:

![GIANI Detect Spots Preview](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Detect_Spots_Preview.PNG)

## Want To Learn More?

See [the Wiki](https://github.com/djpbarry/Giani/wiki) for installation instructions and full documentation.
