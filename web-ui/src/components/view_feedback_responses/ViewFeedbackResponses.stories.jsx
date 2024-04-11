import React from "react";
import { BrowserRouter } from "react-router-dom";
import ViewFeedbackResponses from "./ViewFeedbackResponses";
import {AppContextProvider} from "../../context/AppContext";

export default {
  title: "Check Ins/ViewFeedbackResponses",
  component: ViewFeedbackResponses,
  decorators: [(ViewFeedbackResponses) => {
    return (<AppContextProvider><BrowserRouter><ViewFeedbackResponses/></BrowserRouter></AppContextProvider>);
  }]
};
const Template = (args) => <ViewFeedbackResponses {...args} />;

export const DefaultTemplate = Template.bind({});
DefaultTemplate.args = {
  questions : [
    {
        id: 1,
        questionContent: "What is your current knowledge about opossums?",
        orderNum: 1,

    },
    {
        id: 2,
        questionContent: "Do you think opossums are misunderstood creatures? Why?",
        orderNum: 2,

    },
    {
      id: 3,
      questionContent: "If you knew that opossums didn't carry rabies or other common 'vermin' diseases, are often very docile, and can eat up to 5,000 ticks a season, would your opinion change about opossums?",
      orderNum: 3,

  },


],
//note that submitter name will not be in actual returned object, but this is intermediary for time  being without api
responses: [
    {
        answer: "I don't know that much about opossums",
        questionId: 1,
        responderName: "Erin Deeds",
        sentiment: 0.5,

    },
    {
        answer: "I love opossums. I have rehabilitated baby opossums for 25 years, and I intend to do so until my last day!",
        questionId: 1,
        responderName: "Job Johnson",
        sentiment: 0.8
    },

    {
        answer: "I always thought they were sort of nasty creatures...",
        questionId: 2,
        responderName: "Erin Deeds",
        sentiment: 0.2

    },
    {
        answer: "Opossums are very misunderstood. People think they are dirty and diseased, but their drooling and hissing is a defense mechanism. They eat all kinds of pests, like ticks and mice, keeping disease down. They are wonderful critters!",
        questionId: 2,
        responderName: "Job Johnson",
        sentiment: 0.9,
    },
    {
      answer: "I never knew that about opossums. I think my opinion of them is a little better now.",
      questionId: 3,
      responderName: "Erin Deeds",
      sentiment: 0.5,

  },
  {
      answer: "I already knew that opossums were great.",
      questionId: 3,
      responderName: "Job Johnson",
      sentiment: 0.7,
  }

]
    

};

export const SecondTemplate = Template.bind({});
SecondTemplate.args = {
  questions: [
    {
        id: 1,
        questionContent: "How would you rate the overall technical skill of Joe Johnson? Please elaborate with examples if possible.",
        orderNum: 1,

    },
    {
        id: 2,
        questionContent: "How would you rate the overall ease of communication and dialogue with Joe Johnson? Please elaborate with examples if possible.",
        orderNum: 2,

    },


],

//note that submitter name will not be in actual returned object, but this is intermediary for time  being without api
responses: [
    {
        answer: "Joe's implementations are easily maintainable and easy to understand. My only complaint is that he does not really label methods with descriptive Javadocs as much as he could, confusing some of our engineers.",
        questionId: 1,
        responderName: "Erin Deeds",
        sentiment: 0.6,

    },
    {
        answer: "Joe is an engineer. He sometimes knows how to solve some problems.",
        questionId: 1,
        responderName: "Job Johnson",
        sentiment: 0.5
    },

    {
        answer: "Joe could work better on his communication skills. I understand that he is a senior engineer " +
            "who has been developing these sort of projects for a while, but that doesn't mean he should just " +
            "implement what he thinks is best without talking to the rest of the team (especially on our side).",
        questionId: 2,
        responderName: "Erin Deeds",
        sentiment: 0.2

    },
    {
        answer: "Joe should definitely take a leaf from business and learn to actually have a dialogue with others. "
            + "He is so mysterious that I can't even figure out what I am supposed to do!",
        questionId: 2,
        responderName: "Job Johnson",
        sentiment: 0.2,
    }

]
  
}
