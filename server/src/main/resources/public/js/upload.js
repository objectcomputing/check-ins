

function updateName(){

    /* gets the number of files */
    let nBytes = 0;
        oFiles = document.getElementById("file").files;
        nFiles = oFiles.length;

    for (let nFileId = 0; nFileId < nFiles; nFileId++) {
        nBytes = oFiles[nFileId].name;
    }
    let sOutput = nBytes;



    if(nFiles > 1){
        document.getElementById("filesName").innerHTML = nFiles + " Files Uploaded";
    } else if (nFiles > 0) {
        document.getElementById("filesName").innerHTML = sOutput;
    } else {
        document.getElementById("filesName").innerHTML = "Choose a file";
    }

}












