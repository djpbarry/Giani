Background Subtraction Prior to Nuclear Segmentation
****************************************************

.. figure:: ../images/GIANI_Background_Subtraction_Nuclei.PNG
   :alt: Background subtraction for nuclear segmentation

If your images contain a highly uneven background, it may be necessary
to perform some background subtraction to aid nuclear segmentation.
This may be achieved here using a `Top Hat
filter <https://imagej.net/plugins/morpholibj#top-hats>`__. Background
subtraction is on by default; use the *Enable Top Hat Filtering* toggle to
switch it off if your background is already even.

The *Top Hat Filter Radius for Nuclear Channel* should be roughly equivalent
to the largest object in the image you wish to detect (in other words,
approximately the largest radius you expect a nucleus would have). The
default is ``10.0``. Above you see the result of such an operation. The
upper image shows the output of the :doc:`Gaussian filtering
<filter_nuclei>` stage, with the output of background subtraction shown
below - the contrast has been adjusted to illustrate the subtle difference
in background intensities. The output of this operation will be used in the
next step, the aim of which is complete segmentation of the nuclei.

However, for large radii, this process can take a long time to run. It
is therefore advisable to scale down the image prior to background
subtraction, specified using the parameter *Downsizing Factor for Top Hat
Filter for Nuclear Channel* - a value of 1 equates to no downsizing. There
will be a trade off in terms of accuracy, but this slight reduction in
output quality comes with the benefit of a substantial reduction in
execution time.

.. TODO(screenshot): recapture GIANI_Background_Subtraction_Nuclei.PNG so the
   panel shows the Enable Top Hat Filtering toggle.

When you are happy with the level of background subtraction applied,
click next to proceed to the next step, :doc:`nuclear segmentation
<segment_nuclei>`.
