import React, { useContext } from "react";
import { AppContext } from "../../context/AppContext";
import PdfIcon from '@material-ui/icons/PictureAsPdf';
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';
import List from '@material-ui/core/List';
import "./GuidesPanel.css";
import GuideLink from "./GuideLink";

const GuidesPanel = () => {

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

  return (
    <Card>
      <CardHeader avatar={<PdfIcon />} title="Team Member Resources" />
      <List dense>
        {teamMemberPDFs.map((memberPDF) => (
          <GuideLink key={memberPDF.name} name={memberPDF.name} />
        ))}
      </List>
    </Card>
  );
};

export default GuidesPanel;
