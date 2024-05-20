import { styled } from '@mui/material/styles';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { IconButton } from '@mui/material';

const ExpandMore = styled(props => {
  const { expand, ...other } = props;
  return (
    <IconButton {...other}>
      {props.children ? props.children : <ExpandMoreIcon />}
    </IconButton>
  );
})(({ theme, expand }) => ({
  transform: !expand ? 'rotate(0deg)' : 'rotate(180deg)',
  marginLeft: 'auto',
  transition: theme.transitions.create('transform', {
    duration: theme.transitions.duration.shortest
  })
}));

export default ExpandMore;
