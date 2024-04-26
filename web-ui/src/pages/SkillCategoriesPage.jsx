import React, { useCallback, useContext, useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';

import { AppContext } from '../context/AppContext';

import fileDownload from 'js-file-download';

import {
  Button,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  TextField,
  Tooltip,
  Typography
} from '@mui/material';
import SkillCategoryCard from '../components/skill-category-card/SkillCategoryCard';

import './SkillCategoriesPage.css';
import { selectCsrfToken, selectOrderedSkills } from '../context/selectors';
import {
  createSkillCategory,
  deleteSkillCategory,
  getSkillCategories,
  getSkillsCsv
} from '../api/skillcategory';
import SkillCategoryNewDialog from '../components/skill-category-new-dialog/SkillCategoryNewDialog';
import { UPDATE_TOAST } from '../context/actions';
import Dialog from '@mui/material/Dialog';
import InputAdornment from '@mui/material/InputAdornment';
import { Search } from '@mui/icons-material';
import Autocomplete from '@mui/material/Autocomplete';
import DownloadIcon from '@mui/icons-material/FileDownload';

const PREFIX = 'SkillCategoriesPage';
const classes = {
  root: `${PREFIX}-root`
};

const Root = styled('div')({
  [`&.${classes.root}`]: {
    backgroundColor: 'transparent',
    margin: '4rem 2rem 2rem 2rem',
    height: '100%',
    maxWidth: '100%',
    '@media (max-width: 800px)': {
      display: 'flex',
      flexDirection: 'column',
      overflowX: 'hidden',
      margin: '2rem 5% 0 5%'
    }
  }
});

const SkillCategoriesPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);

  const [skillCategories, setSkillCategories] = useState([]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [categoryToDelete, setCategoryToDelete] = useState(null);
  const [query, setQuery] = useState('');
  const [skillFilter, setSkillFilter] = useState(null);

  useEffect(() => {
    const url = new URL(location.href);

    const addNew = url.searchParams.get('addNew');
    setDialogOpen(addNew === 'true');

    const search = url.searchParams.get('search') || '';
    setQuery(search);

    const filter = url.searchParams.get('filter');
    const skill = skills.find(s => s.name === filter) || null;
    setSkillFilter(skill);
  }, []);

  useEffect(() => {
    const url = new URL(location.href);
    let newUrl = url.origin + url.pathname;
    const params = {};
    if (query) params.search = query;
    if (dialogOpen) params.addNew = true;
    if (skillFilter) params.filter = skillFilter.name;
    if (Object.keys(params).length) {
      newUrl += '?' + new URLSearchParams(params).toString();
    }
    history.replaceState(params, '', newUrl);
  }, [dialogOpen, query, skillFilter]);

  const retrieveCategories = useCallback(async () => {
    if (csrf) {
      const res = await getSkillCategories(csrf);
      const data = res.error ? [] : res.payload.data;
      setSkillCategories(data);
    }
  }, [csrf]);

  useEffect(() => {
    retrieveCategories();
  }, [retrieveCategories]);

  const deleteCategory = useCallback(async () => {
    if (categoryToDelete) {
      const res = await deleteSkillCategory(categoryToDelete.id, csrf);
      if (res.payload.status !== 200) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'Failed to remove skill from category'
          }
        });
      }

      retrieveCategories().then(() => {
        setCategoryToDelete(null);
      });
    }
  }, [categoryToDelete, csrf, dispatch, retrieveCategories]);

  const createNewSkillCategory = async (categoryName, categoryDescription) => {
    const newSkillCategory = {
      name: categoryName.trim(),
      description: categoryDescription
    };

    let res = await createSkillCategory(newSkillCategory, csrf);
    if (!res.error) {
      retrieveCategories();
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'Error: Could not save category'
        }
      });
    }
  };

  const downloadSkills = useCallback(async () => {
    let res = await getSkillsCsv(csrf);
    if (!res.error && res.payload.data) {
      fileDownload(res.payload.data, 'skill_records.csv');
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: 'Skills successfully exported'
        }
      });
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'Failed to export skills'
        }
      });
    }
  }, [csrf, dispatch]);

  const getFilteredCategories = useCallback(() => {
    if (skillCategories) {
      return skillCategories.filter(category => {
        let nameMatches = true;
        if (query) {
          const sanitizedQuery = query.toLowerCase().trim();
          nameMatches = category.name.toLowerCase().includes(sanitizedQuery);
        }

        let skillMatches = true;
        if (skillFilter) {
          skillMatches = category.skills.find(
            skill => skill.name === skillFilter.name
          );
        }

        return nameMatches && skillMatches;
      });
    }

    return [];
  }, [skillCategories, query, skillFilter]);

  return (
    <Root className={classes.root}>
      <div className="skill-categories-header">
        <Typography variant="h4">Skill Categories</Typography>
        <div className="skill-categories-actions">
          <Tooltip
            className="download-skills-button"
            title="Download Skills"
            arrow
          >
            <IconButton onClick={downloadSkills}>
              <DownloadIcon />
            </IconButton>
          </Tooltip>
          <TextField
            className="search-skill-categories-field"
            style={{ width: 'auto', minWidth: '200px', maxWidth: '300px' }}
            label="Search"
            placeholder="Category name"
            variant="outlined"
            size="small"
            value={query}
            onChange={event => setQuery(event.target.value)}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end" color="gray">
                  <Search />
                </InputAdornment>
              )
            }}
          />
          <Autocomplete
            className="filter-skill-categories-field"
            size="small"
            style={{ minWidth: '200px', maxWidth: '300px' }}
            options={skills}
            getOptionLabel={option => option.name}
            filterSelectedOptions
            value={skillFilter}
            onChange={(_, newValue) => setSkillFilter(newValue)}
            renderInput={params => (
              <TextField
                {...params}
                label="Filter by Skill"
                variant="outlined"
                placeholder="Skill name"
              />
            )}
          />
          <Button
            className="new-skill-category-button"
            style={{ whiteSpace: 'nowrap' }}
            variant="contained"
            onClick={() => setDialogOpen(true)}
          >
            New Category
          </Button>
        </div>
      </div>
      {getFilteredCategories().map(category => (
        <SkillCategoryCard
          key={category.id}
          id={category.id}
          name={category.name}
          description={category.description}
          skills={category.skills}
          onDelete={() => setCategoryToDelete(category)}
        />
      ))}
      <SkillCategoryNewDialog
        isOpen={dialogOpen}
        onClose={() => setDialogOpen(false)}
        onConfirm={(categoryName, categoryDescription) => {
          createNewSkillCategory(categoryName, categoryDescription).then(() =>
            setDialogOpen(false)
          );
        }}
      />
      {categoryToDelete && (
        <Dialog
          open={!!categoryToDelete}
          onClose={() => setCategoryToDelete(null)}
        >
          <DialogTitle>Delete Category?</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Are you sure you want to delete the category "
              {categoryToDelete.name}"? The skills in this category will not be
              deleted.
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setCategoryToDelete(null)} color="primary">
              Cancel
            </Button>
            <Button onClick={deleteCategory} color="error" autoFocus>
              Delete
            </Button>
          </DialogActions>
        </Dialog>
      )}
    </Root>
  );
};

export default SkillCategoriesPage;
