import PdfIcon from "@material-ui/icons/PictureAsPdf";
import React from "react";
import "./GuideLink.css";

const GuideLink = (props) => {
    const path = "/pdfs/";
    const fileName = props.name;
    let fullPath = path + fileName + ".pdf";
    return (
        <div className="guide-info">
            <div className="guide-icon">
                <PdfIcon fontSize="large" />
            </div>
            <div className="guide-link">
                <a href={fullPath} target="_blank" rel="noopener noreferrer">
                {fileName}
                </a>
            </div>
            
        </div>
    );
};

export default GuideLink;