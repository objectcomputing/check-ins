import React from 'react';
import PasswordStrength from './PasswordStrength';
import styled from 'styled-components';

const PasswordStrengthWrapper = styled.div`
  margin: 0.8rem 0.8rem 0;
`;

function PasswordModule({
  invalidFirstPasswordCheck,
  hasLength,
  containsUppercase,
  containsNumber,
  containsSymbol,
  minLength,
  password
}) {
  return (
    <>
      <div className="password-reset-form">
        {invalidFirstPasswordCheck && (
          <div className="criteria-line">
            <span className={hasLength ? 'criteria satisfied' : 'criteria'}>
              12+ chars
            </span>
            <span
              className={containsUppercase ? 'criteria satisfied' : 'criteria'}
            >
              uppercase
            </span>
            <span
              className={containsNumber ? 'criteria satisfied' : 'criteria'}
            >
              number
            </span>
            <span
              className={containsSymbol ? 'criteria satisfied' : 'criteria'}
            >
              symbol
            </span>
          </div>
        )}
        <PasswordStrengthWrapper>
          <PasswordStrength minLength={minLength} password={password} />
        </PasswordStrengthWrapper>
      </div>
    </>
  );
}

export default PasswordModule;
