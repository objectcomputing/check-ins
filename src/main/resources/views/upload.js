var files = [];




function updateName(){

    /* gets the number of files */
    let nBytes = 0;
        oFiles = document.getElementById("file").files;
        nFiles = oFiles.length;

    for (let nFileId = 0; nFileId < nFiles; nFileId++) {
        nBytes += oFiles[nFileId].name;
    }
    let sOutput = nBytes + "file Name";



    document.getElementById("fileNum").innerHTML = nFiles;
    document.getElementById("fileName").innerHTML = sOutput;


}









