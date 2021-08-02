import React from "react";
import TemplatePreviewModal from "./TemplatePreviewModal";
import { AppContextProvider } from "../../context/AppContext";


it("renders the template preview modal component.", () => {
  shallowSnapshot(
    <AppContextProvider >
      <TemplatePreviewModal
        open = {true}
        template = {{
          id: '97b0a312-e5dd-46f4-a600-d8be2ad925bb',
          title: 'Survey 1',
          description: 'Make a survey with a few questions'
        }}
      />
    </AppContextProvider >
  );
});