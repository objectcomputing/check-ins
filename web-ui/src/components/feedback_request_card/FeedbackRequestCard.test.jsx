import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AppContextProvider } from '../../context/AppContext';
import FeedbackRequestCard from './FeedbackRequestCard';

it('renders correctly', () => {
  snapshot(
    <BrowserRouter>
      <AppContextProvider>
        <FeedbackRequestCard
          sortType="sent_date"
          dateRange="3mo"
          requesteeId="b2d35288-7f1e-4549-aa2b-68396b162490"
          templateName="Sample Template"
          responses={[
            {
              id: 'c15961e4-6e9b-42cd-8140-ece9efe2445c',
              creatorId: '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498',
              requesteeId: 'b2d35288-7f1e-4549-aa2b-68396b162490',
              recipientId: '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d',
              templateId: '97b0a312-e5dd-46f4-a600-d8be2ad925bb',
              sendDate: [2020, 7, 7],
              dueDate: null,
              submitDate: [2020, 7, 8],
              status: 'submitted'
            }
          ]}
        />
      </AppContextProvider>
    </BrowserRouter>
  );
});
