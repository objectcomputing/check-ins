import React, { useContext } from "react";
import { AppContext } from "../../context/AppContext";
import PdfIcon from '@material-ui/icons/PictureAsPdf';
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';
import List from '@material-ui/core/List';
import "./GuidesPanel.css";
import GuideLink from "./GuideLink";

const PDLGuidesPanel = () => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const isPdl =
    userProfile &&
    userProfile.role &&
    userProfile.role.length > 0 &&
    userProfile.role.includes("PDL");

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

  return isPdl ? (
    <Card>
      <CardHeader avatar={<PdfIcon />} title="Development Lead Guides" />
      <List dense>
        {pdlPDFs.map((pdlPDF) => (
          <GuideLink key={pdlPDF.name} name={pdlPDF.name} />
        ))}
      </List>
    </Card>
  ) : null;
};

export default PDLGuidesPanel;
