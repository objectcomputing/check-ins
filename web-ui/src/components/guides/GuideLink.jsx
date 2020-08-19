import PdfIcon from "@material-ui/icons/PictureAsPdf";
import React from "react";

const GuideLink = (props) => {
    const path = "../../../pdfs/";
    const fileName = props.name;
    let fullPath = path + fileName + ".pdf";
    return (
        <li>
            <PdfIcon />
            <a href={fullPath} target="_blank" rel="noopener noreferrer">
                {fileName}
            </a>
        </li>
    );
};

export default GuideLink;