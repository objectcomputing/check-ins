import React, { useContext } from "react";
import { AppContext } from "../../context/AppContext";
import "./GuidesPanel.css";
import GuideLink from "./GuideLink"

const GuidesPanel = () => {
    const { state } = useContext(AppContext);
    const isPdl = state.user.role === "pdl";

    const teamMemberPDFs = [
        {
            name: "Expectations Discussion Guide for Team Members",
        },
        {
            name: "Expectations Worksheet",
        },
        {
            name: "Feedback Discussion Guide for Team Members",
        },
        {
            name: "Development Discussion Guide for Team Members",
        },
        {
            name: "Individual Development Plan",
        },
    ];

    const pdlPDFs = [
        {
            name: "Development Discussion Guide for PDLs",
        },
        {
            name: "Expectations Discussion Guide for PDLs",
        },
        {
            name: "Feedback Discussion Guide for PDLs",
        },
    ];

    return(
        <fieldset className="guide-container">
            <legend>Check-In Guides</legend>
            <div>
                {teamMemberPDFs.map((memberPDF) =>
                    <GuideLink name={memberPDF.name} />
                )}
                {isPdl &&
                    pdlPDFs.map((pdlPDF) =>
                        <GuideLink name={pdlPDF.name} />
                    )
                }
            </div>
        </fieldset> 
    )
};

export default GuidesPanel;