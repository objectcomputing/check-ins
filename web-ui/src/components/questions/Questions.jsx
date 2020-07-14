import React, { useState } from "react";
import "./Questions.css";

const Questions = ({ onSelect, questions }) => {
  const [answers, setAnswers] = useState({});

  const onChange = (question, e) => {
    const newAnswers = { ...answers, [question.id]: e.target.value };
    setAnswers(newAnswers);
    onSelect(newAnswers);
  };

  return (
    <div className="questions">
      {questions.map((question, i) => (
        <div key={i} style={{ margin: "10px" }}>
          <h4>
            {i + 1}. {question.question}
          </h4>
          <textarea onInput={(e) => onChange(question, e)} wrap="soft" />
        </div>
      ))}
    </div>
  );
};

export default Questions;
