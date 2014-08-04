////Constants////
const eraseBoundary = 30;    //percentage of screen traversed before erasing
const fadeBoundary = 5;      //percentage of screen traversed before fading
const fadeSpeed = 1.5;       //fade-out factor
const enableOpacity = true;  //enables fade effect
const enableRedShade = true; //enables red delete warning
const snapTime = "250ms";    //animation time for snap-back

////prep debug space////
window.addEventListener('load', function(){
  debugTxt = document.getElementById("debug");
});

var debugTxt; //debugging field

////Add element to List////
var idCounter = 0; //tracks current element-to-add

function populate(target)
{
  //create the new list item
  var newDiv = document.createElement('div');                 //create the new element
  newDiv.className = 'listItem';                              //add class name
  newDiv.setAttribute('id', 'item' + idCounter);              //add numbered id
  
  //event listeners
  newDiv.addEventListener('touchstart', touchStart, false);   //touchscreen contact
  newDiv.addEventListener('touchmove', touchMove, false);     //dragging
  newDiv.addEventListener('touchend', touchEnd, false);       //touch released
  newDiv.addEventListener('touchcancel', touchCancel, false); //touch out-of-bounds

  //insert element
  document.getElementById(target).insertBefore(newDiv, null);
  
  //increment counter (prep for next run)
  idCounter++;
};

////Touch related variables////
var touchX;       //The current touched X-coordinate
var touchY;       //The current touched Y-coordinate
var initTouchX;   //The initial X-coordinate
var initTouchY;   //The initial Y-coordinate
var initLocation; //The initial location of the touched object

////The element is touched////
function touchStart(e)
{
  //Store location of initial touch
  var x = parseInt(e.changedTouches[0].clientX); //pull x from event
  var y = parseInt(e.changedTouches[0].clientY); //pull y from event
  touchX = x;
  touchY = y;
  initTouchX = x;
  initTouchY = y;
  
  initLocation = e.target.getBoundingClientRect();
  
  e.preventDefault();
};

////The element is being dragged////
function touchMove(e)
{
  //Update touch location
  touchX = parseInt(e.changedTouches[0].clientX);
  touchY = parseInt(e.changedTouches[0].clientY);
  
  //Move element horizontally
  var element = e.target;                              //The touched element
  var deltaX = touchX - initTouchX;                    //The change-in-x
  var currentX = element.getBoundingClientRect().left; //The left border of the object
  var newX = currentX + deltaX;                        //The translated left border of the object
  initTouchX = touchX;                                 //Reset initial location to prepare for next move
  element.style.left = (newX + "px");                  //Set the new location
  
  //Release transition restriction
  element.style.transitionDuration = "0s";
  
  //Apply opacity filter
  if(enableOpacity)
    {
      var screenWidth = document.body.clientWidth;                 //Get the width of the device
      var fadePoint = screenWidth * fadeBoundary / 100;            //Get the boundary point beyond which fade will start
      var fadeRange = screenWidth - fadePoint;                     //Get the available fading zone (fade point to edge)
      var fadeOverlap = newX - fadePoint;                          //Get the overlap of object with fade zone
      var newFade;                                                 //The new opacity of the object
      if(newX <= fadePoint) newFade = 1.0;                         //No fade if no overlap
      else newFade = 1.0 - (fadeSpeed * fadeOverlap / fadeRange);  //Calculate reduced opacity (factored by fadeSpeed)
      element.style.opacity = newFade;                             //Apply new opacity
      
      //Debugging information
      var testString = screenWidth + "\n" + fadePoint + "\n" + fadeRange + "\n" + fadeOverlap + "\n" + newFade + "\n" + canErase(e);
      debugTxt.innerHTML = testString; //Display debug info
    }
  //Shade red when deletable
  if(enableRedShade)
    {
      if(canErase(e))
      {
        element.style.border = "5px outset red";
      }
      else element.style.border = "";
    }
  
  e.preventDefault();
};

////Touch released within element bounds////
function touchEnd(e)
{
  var element = e.target;
  if(canErase(e)) element.parentNode.removeChild(element); //Delete
  else 
  {
    touchCancel(e); //Treat as failed touch
  }
  
  resetVariables();
};

////Touch failed (out-of-bounds, etc.)////
function touchCancel(e)
{
  var element = e.target;                                               //Store target element
  element.style.opacity = 1.0;                                          //Restore opacity
  element.style.transitionDuration = snapTime;                          //Restore (allow) slow bounce-back
  element.style.left = element.parentNode.getBoundingClientRect().left; //Restore x-position

  element.style.border = "";                     //Clear the border style (remove red)
  
  resetVariables(); //Restore variables related to drag
};

////Check if element is in erase area////
function canErase(e)
{
  var screenWidth = document.body.clientWidth;          //Get screen width
  var erasePoint = screenWidth * eraseBoundary / 100;   //Get erase boundary
  var currentX = e.target.getBoundingClientRect().left; //Get current location
  return (currentX > erasePoint);                       //Return whether or not item is erasable
}

////Clear variables used during dragging////
function resetVariables()
{
  touchX = null;
  touchY = null;
  initTouchX = null;
  initTouchY = null;
  initLocation = null;
}
