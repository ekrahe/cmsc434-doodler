# cmsc434-doodler
## Overview
This is an Android project I made, as you may have expected, for CMSC 434: Introduction to Human-Computer Interaction.
In it, you can draw to your heart's content, or take a photo and draw on that.
You can then share those creations through any app that allows you to send images.
I've included undo, redo, and clear functionality to support all of us perfectionists,
and there's a settings page to change the brush you're drawing with - complete with a preview of what the brush will look like.

I also added in support for some fun multitouch inputs - you can still only draw one line at a time,
but you can create perfectly straight lines that jump from one finger to the other.
There's also a randomize button that takes the difficult choice out of your hands and gives you a new random color for your brush.
Finally, I let the user create dots, which was trivial, but not implemented in the tutorial.

## Specifics
This application runs on Android 5.1.1, which is API 22. I've been running it on my phone, so while the toolbar looks nice for me,
it *may* be too busy on screens with lower resolutions. I wanted the undo, redo, and clear buttons to be accessible
without the need of navigating a menu, so I believe it's worth the risk.

## References
I had fun building this app, and was ambitious with the features I added to the base drawing ability.
This required a lot of outside help, coming from:

* https://youtu.be/ktbYUrlN_Ws
  * A very useful tutorial to get the initial prototype up and running (from my professor)
* http://stackoverflow.com/a/10160671
  * A nice, smooth way of drawing Paths that I happened across, and liked the results of
* http://developer.android.com/training/appbar/index.html
  * Managing the toolbar
* http://stackoverflow.com/a/19882555
  * Managing the icons in the toolbar, so that I could gray out actions when they're unavailable
* http://stackoverflow.com/a/7404421
  * Fixed a silly issue with SeekBars where the thumb was not centered on the bar, but instead floating above it
* http://developer.android.com/training/sharing/index.html
  * Sharing images to other apps
* http://stackoverflow.com/a/20523934
  * Creating the directory where I save the images to be shared
* http://stackoverflow.com/a/3527902
  * Saving the drawn contents of my DoodleView to a JPEG file
* http://stackoverflow.com/a/28078703
  * Filled in the gaps present in the sharing tutorial: How do I get the Uri of my image?
* http://developer.android.com/training/camera/photobasics.html
  * Taking photos within my app

## What I Didn't Reference
My app mostly looks nice due to the template that I used: a NavigationDrawerActivity instead of an EmptyActivity.
I'd like to think that I added some nice details, though.
Most of the code related to the toolbar and navigation drawer were already present in the template,
and I was able to understand the syntax of those pieces and edit them to reflect what I wanted my app to look like.

This design was close to my initial idea, but Android Studio was not able to give me a preview of the Activity within its WYSIWYG editor.
Thus, this app started as a branch of one that more closely reflected the initial tutorial,
while I was iteratively testing the layout by running the app on my phone.

Hence the TestApp project name. Don't worry, it's still called Doodler when installed.
