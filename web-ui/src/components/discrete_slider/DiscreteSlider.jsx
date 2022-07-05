import React from 'react';
import { styled } from '@mui/material/styles';
import Typography from '@mui/material/Typography';
import Slider from '@mui/material/Slider';
import Tooltip from '@mui/material/Tooltip';

const Root = styled('div')(({theme}) => ({
  '& .MuiSlider-markLabel': {
    [theme.breakpoints.down('md')]: {
      fontSize: "0.525rem",
    },
    [theme.breakpoints.between('sm', 'lg')]: {
      fontSize: "0.525rem",
    },
    [theme.breakpoints.between('md', 'xl')]: {
      fontSize: "0.775rem",
    },
    [theme.breakpoints.up('lg')]: {
      fontSize: "0.875rem",
    },
  }
}));

const DiscreteSlider = ({title, onChange, onChangeCommitted, inMarks, inStartPos}) => {
  const defaultMarks = [
    {
      value: 1,
      label: 'Interested',
      tooltip: 'You have an interest in this skill and, perhaps, common knowledge or an understanding of its basic techniques and concepts.',
      tooltipChildren: [
        'Focus on learning.',
      ],
    },
    {
      value: 2,
      label: 'Novice',
      tooltip: 'You have the level of experience gained in a classroom, experimental scenarios, or via other training. You are expected to need help when performing this skill.',
      tooltipChildren: [
        'Focus on developing through experience.',
        'You understand and can discuss terminology, concepts, principles and issues related to this skill.',
        'You rely heavily on reference and resource materials to be effective with this skill.',
      ],
    },
    {
      value: 3,
      label: 'Intermediate',
      tooltip: 'You are able to successfully complete tasks in this competency as requested. Help from an expert may be required from time to time, but you can usually perform the skill independently.',
      tooltipChildren: [
        'Focus on applying and enhancing knowledge or skill.',
        'You have applied this competency to situations, while occasionally needing minimal guidance to perform successfully.',
        'You understand and can discuss the application and implications of changes to processes and procedures in this area.',
      ],
    },
    {
      value: 4,
      label: 'Advanced',
      tooltip: 'You can perform the actions associated with this skill without assistance. You are certainly recognized within your immediate organization as "a person to ask" when difficult questions arise regarding this skill.',
      tooltipChildren: [
        'Focus on broad organizational/professional issues.',
        'You have consistently provided relevant ideas and perspectives on process or practice improvements as relate to this skill.',
        'You are capable of coaching others in the application of this skill.',
        'You participate in senior level discussions regarding this skill.',
        'You assist in the development of reference and resource materials related to this skill.',
      ],
    },
    {
      value: 5,
      label: 'Expert',
      tooltip: 'You are known as an expert in this area. You can provide guidance, troubleshoot and answer questions related to this skill.',
      tooltipChildren: [
        'Focus on the strategic.',
        'You have demonstrated consistent excellence in applying this competency across multiple projects or organizations.',
        'You are considered the “go to” person for this skill within OCI or externally.',
        'You create new applications for or lead the development of reference and resource materials for this skill.',
        'You are able to explain relevant topics, issues, process elements, and trends in sufficient detail during discussions and presentations as to foster a greater understanding among internal and external colleagues.',
      ],
    },
  ];

  const marks = inMarks ? inMarks : defaultMarks;

  const formatTooltipTitle = (title, children) => {
    return(
      <div>
        <div>
          {title}
        </div>
        <ul>
          {
            children && children.map((child) => {
              return(<li key={child}>{child}</li>);
            })
          }
        </ul>
      </div>
    )
  };

  const ValueLabelComponent = (props) => {
    let thisMark = marks.find((mark) => mark.value === props.value);
    if (!thisMark) {
      return '';
    }
    return (
      <Tooltip
        arrow
        open={props.valueLabelDisplay !== "off" && props.open}
        enterTouchDelay={0}
        placement="top"
        title={formatTooltipTitle(thisMark.tooltip, thisMark.tooltipChildren)}
      >
          {props.children}
      </Tooltip>
    );
  };

  const startPos = inStartPos ? Number(inStartPos) : Math.ceil(marks.length/2);

  return (
    <div className="discrete-slider">
      <Typography id="discrete-slider-restrict" gutterBottom>
        {title}
      </Typography>
      <Root>
        <Slider
          min={0.5}
          max={marks.length + 0.5}
          value={startPos}
          valueLabelDisplay="auto"
          components={{
            ValueLabel: ValueLabelComponent
          }}
          getAriaValueText={(value) => `${value}`}
          step={null}
          marks={marks}
          onChange={onChange}
          onChangeCommitted={onChangeCommitted}
        />
      </Root>
    </div>
  );
};

export default DiscreteSlider;