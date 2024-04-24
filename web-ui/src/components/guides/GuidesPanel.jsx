import React from 'react';
import PdfIcon from '@mui/icons-material/PictureAsPdf';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import List from '@mui/material/List';
import './GuidesPanel.css';
import GuideLink from './GuideLink';

const GuidesPanel = () => {
  const teamMemberPDFs = [
    {
      name: 'Expectations Discussion Guide for Team Members'
    },
    {
      name: 'Expectations Worksheet'
    },
    {
      name: 'Feedback Discussion Guide for Team Members'
    },
    {
      name: 'Development Discussion Guide for Team Members'
    },
    {
      name: 'Individual Development Plan'
    }
  ];

  return (
    <Card>
      <CardHeader avatar={<PdfIcon />} title="Team Member Resources" />
      <List dense>
        {teamMemberPDFs.map(memberPDF => (
          <GuideLink key={memberPDF.name} name={memberPDF.name} />
        ))}
      </List>
    </Card>
  );
};

export default GuidesPanel;
