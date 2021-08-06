import React from "react";
import ViewFeedbackPage from "./ViewFeedbackPage";
import {AppContextProvider} from "../context/AppContext";
import {BrowserRouter} from "react-router-dom";

   it("renders correctly", () => {
     snapshot(
         <AppContextProvider>
           <BrowserRouter>
             <ViewFeedbackPage/>
           </BrowserRouter>
         </AppContextProvider>
     );
   });