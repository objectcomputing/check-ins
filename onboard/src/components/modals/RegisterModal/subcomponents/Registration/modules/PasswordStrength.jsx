import React, { useState, useEffect, Fragment } from 'react';
import { defaultOptions, passwordStrength } from 'check-password-strength';
import styled from 'styled-components';

const Bar = styled.div`
  display: inline-block;
  height: 0.3rem;
  flex: 1;
`;

const Strength = styled.div`
  display: flex;
  flex-wrap: nowrap;
  gap: 0.5rem;
`;

const ValueP = styled.p`
  padding-top: 0;
  margin-top: 0;
  margin-bottom: 0.6rem;
  height: 0.8rem;
  font-size: 0.8rem;
`;

function PasswordStrength({ minLength, password }) {
  const COLORS = ['#cc3300', '#ffcc00', '#00cc00', '#009900'];
  const STRENGTHS = ['Too weak', 'Weak', 'Medium', 'Strong'];
  const [value, setValue] = useState('');
  const [strength, setStrength] = useState('');
  const [highlight, setHighlight] = useState('#cc3300');

  const options = defaultOptions;
  options[2].minLength = minLength;
  options[3].minLength = minLength + 3;

  useEffect(() => {
    let newVal = passwordStrength(password, options).value;
    let newStrength = password === '' ? -1 : STRENGTHS.indexOf(newVal);
    let newHighlight = COLORS[newStrength !== -1 ? newStrength : 0];

    setValue(newVal);
    setStrength(newStrength);
    setHighlight(newHighlight);
  }, [password]);

  const getStyle = (index, strength) => {
    const color = index > strength ? '#ddd8d8' : highlight;
    return color;
  };

  return (
    <>
      <ValueP style={password ? { color: highlight } : { color: '#808080' }}>
        {password ? value : 'No entry...'}
      </ValueP>
      <Strength>
        {new Array(0, 1, 2, 3).map((_, index) => {
          let newColor = getStyle(index, strength);
          return (
            <Fragment key={index}>
              <Bar style={{ backgroundColor: newColor }} />
            </Fragment>
          );
        })}
      </Strength>
    </>
  );
}

export default PasswordStrength;
