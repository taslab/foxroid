//Initialize the first button
window.addEventListener('load', function(){
  document.getElementById('initButton').addEventListener('click', scene1);
});

//Sets up the main screen of the app
function scene1()
{
  initParse();
  //remove the unnecessary button
  var kill = document.getElementById('initButton');
  kill.parentNode.removeChild(kill);
  //change the information text
  document.getElementById('textBox').innerHTML = 'Upload or download images using the controls below! The current application is for PNG images.'
  addUploadButton();
  addDownloadButton();
}

//initializes the connection to the parse server
function initParse()
{
  Parse.initialize("5FvwABVcHBm8Q4KxDUNtmlR0htqcPXZNZmaaZKZG", "T9qNW5ni2nar5ylUZgiDSHIKyNpfCWJ8rNuyeDdh");
};

//creates the upload button and file field, and inserts them
function addUploadButton()
{
  var uploadButton = document.createElement('input');
  uploadButton.setAttribute('type', 'file');
  uploadButton.setAttribute('id', 'uploadButton');
  var uploadSubmitButton = document.createElement('button');
  uploadSubmitButton.innerHTML = "Upload";
  uploadSubmitButton.addEventListener('click', uploadFile);
  
  document.getElementById('upload').insertBefore(uploadSubmitButton, null);
  document.getElementById('upload').insertBefore(uploadButton, uploadSubmitButton);
};

//creates and inserts the download button/text field
function addDownloadButton()
{
  var downloadTextBox = document.createElement('input');
  downloadTextBox.setAttribute('type', 'text');
  downloadTextBox.setAttribute('id', 'downloadTextBox');
  var downloadSubmitButton = document.createElement('button');
  downloadSubmitButton.setAttribute('id', 'downloadSubmitButton');
  downloadSubmitButton.innerHTML = 'Download';
  downloadSubmitButton.addEventListener('click', downloadFile);
  
  document.getElementById('download').insertBefore(downloadSubmitButton, null);
  document.getElementById('download').insertBefore(downloadTextBox, downloadSubmitButton);
};

//submits a file and associated object to the Parse server
function uploadFile()
{
  var fileUploadControl = $("#uploadButton")[0];
  if (fileUploadControl.files.length > 0) {
    var file = fileUploadControl.files[0];
    var name = "photo.png";
 
    var parseFile = new Parse.File(name, file);
    parseFile.save().then(function() {
      // The file has been saved to Parse.
      console.log("Image saved successfully.");
    
      var userPhoto = new Parse.Object('UserPhoto');
      userPhoto.set('image', parseFile);
      userPhoto.save().then(function() {
        // Object saved successfully.
        console.log("Object saved successfully.");
        console.log(userPhoto.id);
        document.getElementById("downloadTextBox").value = userPhoto.id;
      }, function(error) {
        // Save error
        console.log("Object save failed.");
      });
    
    }, function(error) {
     // The file either could not be read, or could not be saved to Parse.
     console.log("Image save failed.");
    });
  }
};

//retrieves a file with a given key from the parse server
function downloadFile()
{
  var UserPhoto = Parse.Object.extend("UserPhoto");
  var dlQuery = new Parse.Query(UserPhoto);
  dlQuery.get($("#downloadTextBox").val(), {
    success: function(object) {
      // The object was retreived successfully
      console.log("Download successful");
      displayPhoto(object);
    },
    error: function(model, error) {
      //The object was not retrieved successfully.
      console.log(error.message);
    }
  });
};

//displays the retrieved photo
function displayPhoto(dlObject)
{
  addImage();
  document.getElementById("imageBox").setAttribute('alt', dlObject.id);
  
  var photo = dlObject.get('image');
  console.log(photo.url());
  document.getElementById("imageBox").setAttribute('src', photo.url());
};

//adds the photo display object to the screen
function addImage()
{
  var imageBox = document.createElement('img');
  imageBox.setAttribute('id', 'imageBox');
  
  document.getElementById('image').insertBefore(imageBox, null);
};