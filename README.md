## Solitaire
Drag and drop solitaire.
Written in Java.
Drawn with Javax.Swing / Java.AWT.

![Image of Solitaire](solitaire.png "Solitaire")

#### How to Use:
Compile the game by `cd`ing into the `src` directory and running:
```
javac -d [output directory] Main.java backend/*.java frontend/*.java;
```
Start the game after compilation by `cd`ing into the output directory running:
```
java Main;
```
(or the Windows equivalent).

#### Possible Improvements:
* Currently, the GUI redraws the frame in a tight loop whenever a card is picked up. This uses a lot of CPU and should probably be fixed.

* Change code so that entire stacks of revealed tableau cards can be moved onto other tableau stacks.

Version 1.0 - 4/26/2020
