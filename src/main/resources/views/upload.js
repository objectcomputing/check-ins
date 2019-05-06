
var input = document.getElementById('file');
var infoArea = document.getElementById('file-upload-filename');

input.addEventListener('change', showFileName);

function showFileName(event){

    var input = event.srcElement;

    var fileName = input.file[0].name;

    infoArea.textContent = 'File Name' + fileName;

}







