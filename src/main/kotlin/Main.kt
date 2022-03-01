import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

val body = "body" //used to create body image

//offsets for placing sprites on panel
val offsetx = 80
val offsety = 100

//color sliders
val sliderH = JSlider(JSlider.HORIZONTAL, 0, 99, 99) //hue
val sliderS = JSlider(JSlider.HORIZONTAL, 0, 100, 100) //saturation
val sliderB = JSlider(JSlider.HORIZONTAL, 0, 100, 100) //brightness

//slider labels
val Hlabel = JLabel(" Hue")
val Slabel = JLabel(" Saturation")
val Blabel = JLabel(" Brightness")

//numerical values of color sliders
var Hvalue = 0.99f // hue at 1 is the same as hue at 0, so max is 0.99
var Svalue = 1f
var Bvalue = 1f

//color of body
var bodyCol = Color.getHSBColor(Hvalue, Svalue, Bvalue)

//image of the body, set to the color of the body
var bodyimg = changeColor(body, bodyCol)

// names of all the arrays of items to be customized
var Types = arrayOf("Faces", "Hats", "Shirts", "Pants", "Locations")

// arrays of items, corresponding to file names of the sprites
var Faces = arrayOf("Happy", "Sad", "Angry", "Furious", "Surprised", "Ted")
var Shirts = arrayOf("None", "Shirt", "Hawaiian", "Floatie", "Bowtie")
var Pants = arrayOf("None", "Jeans", "Shorts", "Underwear", "Boots")
var Hats = arrayOf("None", "Tophat", "BaseballCap", "PropellerCap", "PirateHat")
var Locations = arrayOf("Jungle", "Ocean", "Moon", "Beach", "Volcano", "Bedroom")

// the current type being edited, based on whats selected in the dropdown
var currentType = "Faces"

// all the items currently in use, their colors, and their images
var currentFace = "Happy"

//face doesnt change color; sliders on face are instead for the body color
var faceimg: BufferedImage = ImageIO.read(File("sprites/$currentFace.png"))
var currentShirt = "None"
var shirtCol = Color.getHSBColor(Hvalue, Svalue, Bvalue)
var shirtimg = changeColor(currentShirt, shirtCol)
var currentHat = "None"
var hatCol = Color.getHSBColor(Hvalue, Svalue, Bvalue)
var hatimg = changeColor(currentHat, hatCol)
var currentPants = "None"
var pantsCol = Color.getHSBColor(Hvalue, Svalue, Bvalue)
var pantsimg = changeColor(currentPants, pantsCol)
var currentLoc = "Jungle"

//background location doesn't change color
var bgimg: BufferedImage = ImageIO.read(File("sprites/$currentLoc.png"))

//window elements
var wn = JFrame("Monkey Maker") //create JFrame with title
val panel = JPanel() //panel for sliders and dropdowns
var imagePanel = JPanel() //panel for sprite renders
val randombutton = JButton("Randomize") //button for randomizing values
val savebutton = JButton("Save") //button for saving image panel to file
var typedropdown: JComboBox<String> = JComboBox<String>(Types) //dropdown for item type
var itemdropdown: JComboBox<String> = JComboBox<String>(Faces) //dropdown for items of said type
var menubar = JMenuBar() //menu bar at the top for our buttons
var icon: BufferedImage = ImageIO.read(File("sprites/monkeyicon.png"))

fun main(args: Array<String>) {
    EventQueue.invokeLater(::window) //open the window
}

/**
 * Creates the window, panels, and swing elements
 */
fun window() {
    //window setup
    wn.iconImage = icon
    wn.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    wn.setSize(600, 750)
    wn.isVisible = true
    imagePanel = RenderImages() //render images
    wn.contentPane.add(imagePanel, BorderLayout.CENTER)
    wn.jMenuBar = menubar
    menubar.add(savebutton)
    menubar.add(randombutton)
    panel.layout = GridLayout(4, 2, 0, 0)
    panel.add(sliderH)
    panel.add(Hlabel)
    panel.add(sliderS)
    panel.add(Slabel)
    panel.add(sliderB)
    panel.add(Blabel)
    panel.add(typedropdown)
    panel.add(itemdropdown)
    wn.contentPane.add(panel, BorderLayout.NORTH)
    //add listeners to interactive elements
    sliderH.addChangeListener(SliderListener())
    sliderS.addChangeListener(SliderListener())
    sliderB.addChangeListener(SliderListener())
    typedropdown.addActionListener(DropdownListener())
    itemdropdown.addActionListener(DropdownListener())
    savebutton.addActionListener(ButtonListener())
    randombutton.addActionListener(ButtonListener())
}

/**
 * Changes color of a sprite, based off a given string for the image file name and a given color
 */
fun changeColor(item: String, col: Color): BufferedImage {
    if (item != "None") {
        //every image has a mask, a solid-colored shape, and an overlay that contains the rest of the image
        val img: BufferedImage = ImageIO.read(File("sprites/${item}mask.png")) //mask of our image
        val img2: BufferedImage = ImageIO.read(File("sprites/$item.png")) //overlay image
        val newimage = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB) //prep newimage to be drawn to
        val g: Graphics2D = newimage.createGraphics() //graphics element of the image
        g.drawImage(img, 0, 0, null) // draw img to newimage
        g.composite = AlphaComposite.SrcAtop // composite color on top
        g.color = col // set color
        g.fillRect(0, 0, img.width, img.height) // fill image with said color
        g.drawImage(img2, 0, 0, null) // draw overlay image over the base color
        g.dispose() // dont need graphics anymore so get rid of it
        return newimage // return our image
    } else { //string is None, display a 1x1 pixel nothing image
        val newimage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        return newimage
    }
}

/**
 * Randomizes all the item and item color values
 */
fun randomize() {
    val rand = Random() //declare an instance of the random() class
    //random body color
    bodyCol = Color.getHSBColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()) //random floats from 0 to 1
    bodyimg = changeColor(body, bodyCol) //set the color of sprite
    //random face
    currentFace = Faces[rand.nextInt(Faces.size)] //random string from our item arrays
    faceimg = ImageIO.read(File("sprites/$currentFace.png")) //set the sprite
    //random shirt
    currentShirt = Shirts[rand.nextInt(Shirts.size)]
    shirtCol = Color.getHSBColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())
    shirtimg = changeColor(currentShirt, shirtCol)
    //random hat
    currentHat = Hats[rand.nextInt(Hats.size)]
    hatCol = Color.getHSBColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())
    hatimg = changeColor(currentHat, hatCol)
    //random pants
    currentPants = Pants[rand.nextInt(Pants.size)]
    pantsCol = Color.getHSBColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())
    pantsimg = changeColor(currentPants, pantsCol)
    //random background
    currentLoc = Locations[rand.nextInt(Locations.size)]
    bgimg = ImageIO.read(File("sprites/$currentLoc.png"))
    //refresh dropdowns to display the right values
    DropdownListener().refreshType()
    DropdownListener().refreshItems()
    wn.repaint() //repaint everything
}

/**
 * Saves monkey to a PNG image somewhere specified in your files
 */
fun saveImage() {
    val fc = JFileChooser() //open file explorer dialog
    val myval = fc.showDialog(wn, "Save") //its a save function
    var saveit = false //bool to decide whether we actually save it
    if (myval == JFileChooser.APPROVE_OPTION) { //if you chose to save, carry on
        var file = fc.selectedFile //get file name inputted
        val path = fc.currentDirectory.absolutePath //get absolute path of directory they chose
        val fileElem = file.name.split(".") //split file name on .
        try { //check if they put a .png extension. if they didn't, add one
            if (fileElem[1] != ".png") {
                file = File(fileElem[0] + ".png")
            } //if they didnt put any extension, add .png to the filename
        } catch (e: IndexOutOfBoundsException) {
            file = File(fileElem[0] + ".png")
        }
        val fullpath = File(path + '\\' + file.name) //create fullpath from directory and filename
        if (fullpath.exists()) { //check if the file already exists
            val dialog = JOptionPane.showConfirmDialog( //ask user if they would like to overwrite that file
                wn, "The file" + file.name + "already exists in this directory. Do you want to overwrite it?",
                "Hold Up!", JOptionPane.YES_NO_OPTION
            )
            if (dialog == JOptionPane.NO_OPTION) {
                //they chose no, so dont save
            } else if (dialog == JOptionPane.YES_OPTION) {
                saveit = true //they chose to overwrite the file, so we can save it
            }
        } else { //file doesn't already exist so we can save it
            saveit = true
        }
        if (saveit) { //if we can indeed save, do so
            val p = imagePanel //get image panel as new variable
            p.size = Dimension(600, 600) //set size
            val img = BufferedImage(p.width, p.height, BufferedImage.TYPE_INT_RGB) //new image to be painted to
            p.paint(img.graphics) //paint the image panel to the new image
            ImageIO.write(img, "PNG", fullpath) //write image we drew to our path
            JOptionPane.showMessageDialog(
                wn, "Saved image to $fullpath", "Success", JOptionPane.NO_OPTION
            )
        } else {
            //save cancelled, do nothing
        }
    }
}

/**
 * class solely used for drawing images on the JFrame. Extends JComponent
 */
internal class RenderImages : JPanel() {
    /**
     * Draws images by overriding the paint component
     */
    @Override
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.drawImage(bgimg, 0, 0, this) //draws image at 0,0
        g.drawImage(bodyimg, offsetx, offsety, this) // draw images in order at offset values
        g.drawImage(pantsimg, offsetx, offsety, this)
        g.drawImage(shirtimg, offsetx, offsety, this)
        g.drawImage(faceimg, offsetx, offsety, this)
        g.drawImage(hatimg, offsetx, offsety, this)
    }
}

/**
 * Listener class for sliders. When the slider changes, do something.
 */
internal class SliderListener : ChangeListener {
    override fun stateChanged(e: ChangeEvent?) {
        val slider = e!!.source as JSlider //set changed slider to a variable
        if (slider == sliderH) { //change value based on which slider it is
            Hvalue = slider.value.toFloat() * 0.01f
        } //slider values are 1 to 100, but HSB values are 0.00 to 1.00
        else if (slider == sliderS) {                  //thus, we multiply current slider value by 0.01
            Svalue = slider.value.toFloat() * 0.01f
        } else if (slider == sliderB) {
            Bvalue = slider.value.toFloat() * 0.01f
        }

        //depending on what the current type dropdown is, change that sprite's color
        if (currentType == "Faces") {
            bodyCol = Color.getHSBColor(Hvalue, Svalue, Bvalue) //get color from our values
            bodyimg = changeColor(body, bodyCol) //change sprite color
        }
        if (currentType == "Shirts") {
            shirtCol = Color.getHSBColor(Hvalue, Svalue, Bvalue)
            shirtimg = changeColor(currentShirt, shirtCol)
        }
        if (currentType == "Hats") {
            hatCol = Color.getHSBColor(Hvalue, Svalue, Bvalue)
            hatimg = changeColor(currentHat, hatCol)
        }
        if (currentType == "Pants") {
            pantsCol = Color.getHSBColor(Hvalue, Svalue, Bvalue)
            pantsimg = changeColor(currentPants, pantsCol)
        }
        //re-render images, repaint window
        imagePanel = RenderImages()
        imagePanel.revalidate()
        wn.repaint()
    }
}

/**
 * Listener class for dropdowns. When a dropdown is used, do something.
 */
internal class DropdownListener : ActionListener {
    override fun actionPerformed(e: ActionEvent?) {
        val dropdown = e!!.source as JComboBox<*> //set whichever dropdown was changed to a variable
        if (dropdown == typedropdown) { //depending on the dropdown, call it's specific function
            refreshType()
        } else if (dropdown == itemdropdown) {
            refreshItems()
        }
    }

    /**
     * When we switch to another type, set the item to whatever we picked previously
     * and set the color sliders to the color we have saved
     */
    fun refreshType() {
        //current type is whatever dropdown element we chose
        currentType = typedropdown.getItemAt(typedropdown.selectedIndex)
        panel.remove(itemdropdown) //remove item dropdown to set it to the right dropdown values based on type
        var hsb = FloatArray(3) //create empty array of hsb values to be populated
        //depending on the current type, set the selected item and color values to hsb array
        if (currentType == "Faces") {
            itemdropdown = JComboBox<String>(Faces)
            itemdropdown.selectedItem = currentFace
            Color.RGBtoHSB(bodyCol.red, bodyCol.green, bodyCol.blue, hsb)
        } else if (currentType == "Shirts") {
            itemdropdown = JComboBox<String>(Shirts)
            itemdropdown.selectedItem = currentShirt
            Color.RGBtoHSB(shirtCol.red, shirtCol.green, shirtCol.blue, hsb)
        } else if (currentType == "Hats") {
            itemdropdown = JComboBox<String>(Hats)
            itemdropdown.selectedItem = currentHat
            Color.RGBtoHSB(hatCol.red, hatCol.green, hatCol.blue, hsb)
        } else if (currentType == "Pants") {
            itemdropdown = JComboBox<String>(Pants)
            itemdropdown.selectedItem = currentPants
            Color.RGBtoHSB(pantsCol.red, pantsCol.green, pantsCol.blue, hsb)
        }
        if (currentType == "Locations") {
            itemdropdown = JComboBox<String>(Locations)
            itemdropdown.selectedItem = currentLoc
            hsb = floatArrayOf(0f, 0f, 0f)
            sliderH.isEnabled = false //disable sliders, locations don't change color
            sliderS.isEnabled = false
            sliderB.isEnabled = false
        } else {
            sliderH.isEnabled = true //turns sliders back on
            sliderS.isEnabled = true
            sliderB.isEnabled = true
        }
        sliderH.value = (hsb[0] * 100).toInt() //set values of sliders (multiply by 100 since HSB values are 0.00 to
        sliderS.value = (hsb[1] * 100).toInt() // 1.00 instead of 0 to 100)
        sliderB.value = (hsb[2] * 100).toInt()
        panel.add(itemdropdown) //add the new dropdown with new item values
        itemdropdown.addActionListener(DropdownListener()) //add the listener since we removed and readded the dropdown
        wn.revalidate() //reset window to refresh values
    }

    /**
     * When we switch to a different item, render it on the screen and refresh the dropdown.
     */
    fun refreshItems() {
        //depending on what the current type is, render the right image with the current color
        if (currentType == "Faces") {
            currentFace = itemdropdown.getItemAt(itemdropdown.selectedIndex)
            faceimg = ImageIO.read(File("sprites/$currentFace.png"))
        }
        if (currentType == "Shirts") {
            currentShirt = itemdropdown.getItemAt(itemdropdown.selectedIndex)
            shirtimg = changeColor(currentShirt, shirtCol)
        }
        if (currentType == "Hats") {
            currentHat = itemdropdown.getItemAt(itemdropdown.selectedIndex)
            hatimg = changeColor(currentHat, hatCol)
        }
        if (currentType == "Pants") {
            currentPants = itemdropdown.getItemAt(itemdropdown.selectedIndex)
            pantsimg = changeColor(currentPants, pantsCol)
        }
        if (currentType == "Locations") {
            currentLoc = itemdropdown.getItemAt(itemdropdown.selectedIndex)
            bgimg = ImageIO.read(File("sprites/$currentLoc.png"))
        }
        wn.repaint() //repaint whole window
    }
}

/**
 * Listener class for buttons. When a button is clicked, do something.
 */
internal class ButtonListener : ActionListener {
    override fun actionPerformed(e: ActionEvent?) {
        val button = e!!.source as JButton //set button pressed to a new variable
        if (button == savebutton) { //whichever button it is, call it's specific function
            saveImage()
        } else if (button == randombutton) {
            randomize()
        }
    }
}


