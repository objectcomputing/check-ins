import sanitizeHTML from 'sanitize-html';

//This function is a helper function that sanitizes user inputs on
//Quill/rich text editor elements to prevent XSS attacks. It is used in
//Notes and PrivateNotes to prevent bad content from being uploaded to the
//server or rendered on the DOM.
export const sanitizeQuillElements = htmlInput => {
  const cleaner = sanitizeHTML(htmlInput, {
    allowedTags: [
      'b',
      'i',
      'strong',
      'a',
      'h1',
      'h2',
      'h3',
      'h4',
      'h5',
      'h6',
      'em',
      'p',
      'ins',
      'sup',
      'sub',
      'span',
      'mark',
      'small',
      'del',
      'a',
      'ul',
      'ol',
      'li',
      'br',
      'u',
      'blockquote',
      'q',
      'abbr',
      'cite'
    ],
    allowedAttributes: {
      a: ['href', 'target', 'title']
    }
  });
  return cleaner;
};
