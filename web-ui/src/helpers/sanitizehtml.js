import sanitizeHTML from "sanitize-html"

export const sanitizeQuillElements = (htmlInput) => {
    const cleaner = sanitizeHTML(htmlInput, {
        allowedTags: 
        ['b', 
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
        'cite'],
        allowedAttributes: {
          a: ['href', 'target', 'title',]
        }
      });
      console.log(cleaner)
      return cleaner;
    }
