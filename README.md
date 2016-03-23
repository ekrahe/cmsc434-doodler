# cmsc434-doodler
## Overview
This is an Android project I made, as you may have expected, for CMSC 434: Introduction to Human-Computer Interaction. In it, you can draw to your heart's content, or take a photo and draw on that. You can then share those creations through any app that allows you to send images. I've included undo, redo, and clear functionality to support all of us perfectionists, and there's a settings page to change the brush you're drawing with - complete with a preview of what the brush will look like.

I also added in support for some fun multitouch inputs - you can still only draw one line at a time, but you can create perfectly straight lines that jump from one finger to the other. There's also a randomize button that takes the difficult choice out of your hands and gives you a new random color for your brush. Finally, I let the user create dots, which was trivial, but not implemented in the tutorial.

## Usage
This application runs on Android 5.1.1, which is API 22. I've been running it on my phone, so while the toolbar looks nice for me, it *may* be too busy on screens with lower resolutions. I wanted the undo, redo, and clear buttons to be accessible without the need of navigating a menu, so I believe it's worth the risk.

When the user opens the app, they are put directly into the drawing activity. By dragging their finger across the white canvas, they can draw. A single tap will create a dot, and only one line can be drawn at a time.

At the top of this screen is a toolbar with undo, redo, and clear buttons, which do exactly as advertised - though the clear button has a failsafe, and thus needs to be tapped twice in a row to work. There is also a shuffle button that randomizes the color of the brush every time it's pressed. The shuffle button is always active, but the undo/clear/redo buttons will become dim when they are not usable. A good example of this would be if they undid every line they drew, but it's important to note that only the last 15 lines drawn can be undone.

In the top left corner of the screen, there is a menu button that opens up an options drawer. The drawer can also be opened by sliding one's finger from the left side of the screen. There are three options in this menu: Camera, Settings, and Share. The drawer can be exited without choosing any of these options.

Using the Camera redirects the user to the Android Camera interface, where they can take a picture or cancel their action. If one is taken, the current drawing is cleared and the new photo is set as the background of the drawing canvas. To return to a blank canvas, the user only needs to double-tap the clear button, as long as any drawing he or she doodled has already been cleared.

The Settings button redirects the user to a new page, where they're presented with sliders to change brush size, opacity, and the hue, saturation, and brightness values of the brush color. The shuffle button also still resides at the top of the screen. To exit this screen, one can use the system's back button or the back arrow in the top left corner.

Finally, the Share button saves the current drawing, and prompts the user to share it to any social media network. Once a medium is selected, they are redirected to that app's messaging interface.

## References
I had fun building this app, and was ambitious with the features I added to the base drawing ability. This required a lot of outside help, coming from:

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
My app mostly looks nice due to the template that I used: a NavigationDrawerActivity instead of an EmptyActivity. I'd like to think that I added some nice details, though. Most of the code related to the toolbar and navigation drawer were already present in the template, and I was able to understand the syntax of those pieces and edit them to reflect what I wanted my app to look like.

This design was close to my initial idea, but Android Studio was not able to give me a preview of the Activity within its WYSIWYG editor. Thus, this app started as a branch of one that more closely reflected the initial tutorial, while I was iteratively testing the layout by running the app on my phone.

Hence the TestApp project name. Don't worry, it's still called Doodler when installed.
