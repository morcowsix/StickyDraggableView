# StickyDraggableView
Android compose library implementing sticky and draggable behavior for views.

## Usage 

For the library to work properly, two components must be implemented in the code:
Draggable and Holder. Сomponents can be positioned independently, but must be on the same Compose screen.

#### Draggable component:

Сontains: wrapper **DraggableStickyView**, in which the desired Compose is placed and background Compose whick will show when main Compose will drag. 

```kotlin
Box() {
  DraggableStickyView() {
    ComposeFunctionImplementingDraggableView()
  }
  
  ComposeFunctionImplementingBackgroundForDraggableView()
}
```

#### Holder component:

Сontains: wrapper **StickyHolder**, in which the desired Compose is placed and wrapper **Sticker**, in which commonly placed copy of draggable Compose.

```kotlin
StickyHolder() {
  ComposeFunctionImplementingDesiredContent()
  
  Sticker() {
    ComposeFunctionImplementingCopyOfDraggableView()
  }
}
```
