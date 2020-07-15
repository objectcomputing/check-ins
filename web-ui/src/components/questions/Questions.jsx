import React, { useState } from "react";
import "./Questions.css";

const displayName = "Questions";

const Questions = ({ onAnswer, questions }) => {
  const [answers, setAnswers] = useState({});

  const onChange = (question, e) => {
    const newAnswers = { ...answers, [question.id]: e.target.value };
    setAnswers(newAnswers);
    onAnswer(newAnswers);
  };

  return (
    <div className="questions">
      {questions.map((question, i) => (
        <div key={`questions-${question.id}`} style={{ margin: "10px" }}>
          <h4>
            {i + 1}. {question.question}
          </h4>
          <textarea
            id={`questions-input-${question.id}`}
            onInput={(e) => onChange(question, e)}
            wrap="soft"
          />
        </div>
      ))}
    </div>
  );
};

Questions.displayName = displayName;

export default Questions;
