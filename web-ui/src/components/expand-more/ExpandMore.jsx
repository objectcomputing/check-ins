import { styled } from '@mui/material/styles';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { IconButton } from '@mui/material';

const ExpandMore = styled(props => {
  const { expand, ...other } = props;
  return (
    <IconButton classes={{ root: 'expand-more' }} {...other}>
      {props.children ? props.children : <ExpandMoreIcon />}
    </IconButton>
  );
})(({
  theme
}) => ({
  transform: 'rotate(180deg)',
  marginLeft: 'auto',
  transition: theme.transitions.create('transform', {
    duration: theme.transitions.duration.shortest
  }),
  variants: [{
    props: (
      {
        expand
      }
    ) => !expand,
    style: {
      transform: 'rotate(0deg)'
    }
  }]
}));

export default ExpandMore;
