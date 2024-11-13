import React from 'react';
import PropTypes from 'prop-types';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faFaceGrinBeam,
  faSmile,
  faMeh,
  faFrown,
  faFaceSadCry
} from '@fortawesome/free-regular-svg-icons';

const propTypes = {
  message: PropTypes.string,
  onSelect: PropTypes.func
};
const displayName = 'Feelings';

const Feelings = ({ onSelect, message }) => {
  const inputs = [
    ['Terrible', faFaceSadCry],
    ['Bad', faFrown],
    ['Okay', faMeh],
    ['Good', faSmile],
    ['Great', faFaceGrinBeam]
  ];
  const onChange = e => {
    onSelect(e.target.value);
  };

  return (
    <div>
      <h4>{message}</h4>
      <div style={{ display: 'flex' }}>
        {inputs.map(([text, icon], i) => (
          <div
            key={`feelings-${i}`}
            style={{
              margin: '10px',
              display: icon === undefined ? 'flex' : '',
              alignItems: 'flex-end'
            }}
          >
            <div>
              <FontAwesomeIcon icon={icon} size="3x" />
            </div>
            <input
              data-testid={`feelings-input-${i}`}
              id={`feelings-input-${i}`}
              type="radio"
              name="feeling"
              onClick={onChange}
              value={text}
            />
            {text}
          </div>
        ))}
      </div>
    </div>
  );
};

Feelings.propTypes = propTypes;
Feelings.displayName = displayName;

export default Feelings;
