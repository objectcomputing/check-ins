import React, { useContext, useEffect, useState } from 'react';
import PdfIcon from '@mui/icons-material/PictureAsPdf';
import { Card, CardHeader, List } from '@mui/material';

import { AppContext } from '../../context/AppContext.jsx';
import {selectCsrfToken, selectCurrentUserRoles, selectRoles} from '../../context/selectors.js';
import { getDocumentsForRoleId } from '../../api/document.js';

import GuideLink from './GuideLink';
import './GuidesPanel.css';

const fallback = [
  {
    id: '1-member',
    name: 'Expectations Discussion Guide for Team Members',
    url: '/pdfs/Expectations_Discussion_Guide_for_Team_Members.pdf'
  },
  {
    id: '2-member',
    name: 'Expectations Worksheet',
    url: '/pdfs/Expectations_Worksheet.pdf'
  },
  {
    id: '3-member',
    name: 'Feedback Discussion Guide for Team Members',
    url: '/pdfs/Feedback_Discussion_Guide_for_Team_Members.pdf'
  },
  {
    id: '4-member',
    name: 'Development Discussion Guide for Team Members',
    url: '/pdfs/Development_Discussion_Guide_for_Team_Members.pdf'
  },
  {
    id: '5-member',
    name: 'Individual Development Plan',
    url: '/pdfs/Individual_Development_Plan.pdf'
  }
];

const GuidesPanel = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const userRoles = selectCurrentUserRoles(state);
  const allRoles = selectRoles(state);

  const [documents, setDocuments] = useState([]);

  useEffect(() => {
    const getDocuments = async () => {
      const docs = [];
      if(userRoles) {
        for (const roleName of userRoles) {
          const memberRoleId = allRoles.find(role => role.role === roleName)?.id;
          const res = await getDocumentsForRoleId(memberRoleId, csrf);
          const responseBody = res.payload?.data && !res.error
              ? res.payload.data
              : undefined;
          if (responseBody?.length > 0) {
            docs.push(...responseBody);
          }
        }
      }

      setDocuments(docs.length > 0 ? docs : fallback);
    };
    if (csrf) {
      getDocuments();
    }
  }, [allRoles, userRoles, csrf, setDocuments, getDocumentsForRoleId]);

  return (
      <Card>
        <CardHeader avatar={<PdfIcon />} title={"Check-In Resources"} />
        <List dense>
          {documents.map(doc => (
              <GuideLink key={doc.id} id={doc.id} name={doc.name} description={doc.description} url={doc.url} />
          ))}
        </List>
      </Card>
  );;
};

export default GuidesPanel;
