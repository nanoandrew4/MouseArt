# i(nput)Art
This program is modelled after IO Graph, which I saw on the internet and thought would be interesting to try and code, and implement my own ideas into it. The purpose of the program is to track the mouse pointer position and keystrokes to draw various geometric shapes on an invisible canvas, which is later saved to an image. Other shapes appear based on actions or inactions, such as circles.

## Features
- Many color palettes to choose from, to make your images even more artsy!
- Mouse and keyboard tracking, which allows the program to create your awesome art. This information is safe, it is not saved or sent anywhere. It is only used to trigger draw calls, and is subsequently discarded.
- A resolution multiplier, which allows the program to draw bigger (and therefore more beautiful) images, so that zooming in does not cause as much pixelation.
- Preview window which allows real time viewing of the art that is being drawn on the virtual canvas.

## Usage

Run as you would any other java program, either by double clicking it, or by running "java -jar iArt-vX.Y-release.jar" on a command line, in the directory the jar file is located at. When the program is run for the first time, it requests the user to enter their keyboard layout, from top left key, to bottom right. You choose what key will be the top left of the layout, and which the bottom right. Please note that when entering the layout, the keys pressed will be processed by the OS, so don't be surprised if you hit the power key and the system shuts down. Try avoiding those types of keys.

## Sample images

### ColorWheel scheme
![alt text](https://raw.githubusercontent.com/nanoandrew4/iArt/master/sample_art/colorwheel.png "ColorWheel scheme demo")

### GrayscaleWheel scheme
![alt text](https://raw.githubusercontent.com/nanoandrew4/iArt/master/sample_art/grayscalewheel.png "GrayscaleWheel scheme demo")

### Rainbow scheme
![alt text](https://raw.githubusercontent.com/nanoandrew4/iArt/master/sample_art/rainbow.png "Rainbow scheme demo")

### VertColorFall scheme
![alt text](https://raw.githubusercontent.com/nanoandrew4/iArt/master/sample_art/colorfall.png "VertColorFall scheme demo")

## Please note
Although none of the input during the recording session is stored, when the program is launched for the first time, a window will request the user enters their keyboard layout, since there is no way for the program to know otherwise. Once the layout is fully entered, it is saved to the disk, at the users home directory, with the name ".iart_keys". This file is used on subsequent program runs to load the keyboard layout, so the program knows where to draw each of the keystrokes on the virtual canvas. This file is not sent anywhere, it resides solely on disk.

Also worthwhile mentioning that the program has low CPU consumption, unless the program window is focused, in which case the CPU usage spikes. This is due to the constant updating of the preview window. So just click anywhere other than the program window, and it will stop updating the preview, therefore freeing up you CPU.
