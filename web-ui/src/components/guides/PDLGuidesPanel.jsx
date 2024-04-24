import React, { useContext } from 'react';
import { AppContext } from '../../context/AppContext';
import PdfIcon from '@mui/icons-material/PictureAsPdf';
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import List from '@mui/material/List';
import './GuidesPanel.css';
import GuideLink from './GuideLink';

const PDLGuidesPanel = () => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const isPdl =
    userProfile &&
    userProfile.role &&
    userProfile.role.length > 0 &&
    userProfile.role.includes('PDL');

  const pdlPDFs = [
    {
      name: 'Development Discussion Guide for PDLs'
    },
    {
      name: 'Expectations Discussion Guide for PDLs'
    },
    {
      name: 'Feedback Discussion Guide for PDLs'
    }
  ];

  return isPdl ? (
    <Card>
      <CardHeader avatar={<PdfIcon />} title="Development Lead Guides" />
      <List dense>
        {pdlPDFs.map(pdlPDF => (
          <GuideLink key={pdlPDF.name} name={pdlPDF.name} />
        ))}
      </List>
    </Card>
  ) : null;
};

export default PDLGuidesPanel;
