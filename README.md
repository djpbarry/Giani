![GIANI Icon](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GianiIcon.png)

## Introduction

GIANI is a FIJI plugin designed to facilitate routine analysis of 3D cell biology image data. Implemented specifically with batch-processing in mind, GIANI is designed to:

* Read data sets in their native, proprietary
* Segment nuclei & cells
* Measure nuclear and cell morphology, together with fluorescence intensities within these regions.
* Explore results

## Design Philosophy

GIANI has been designed to batch analyse 3D datasets with minimal user interaction. In principle, all that is required to run an analysis is a set of parameters, which can be specified either via the user interface or within an XML file, and an input directory. Using several existing components distributed with FIJI, GIANI then executes a set pipeline, as illustrated below:

![](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANIPhilosophy.PNG)

## Segment Nuclei & Cells

Nuclei locations are estimated using blob detection...

![GIANI Detect Nuclei Centres Preview](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Detect_Nuclei_Centres_Preview.PNG)

...before full segmentation is achieved using a watershed operation...

![Nuclear Segmentation Preview](https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Segment_Nuclei_Preview.PNG)


...and then cells are similarly segmented:

![Cell Segmentation Preview](
https://raw.githubusercontent.com/wiki/djpbarry/Giani/images/GIANI_Segment_Cells_Preview.PNG)

## Documentation

See [the Wiki](https://github.com/djpbarry/Giani/wiki) for full instructions.
