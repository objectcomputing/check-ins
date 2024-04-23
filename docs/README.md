# Check-Ins Online Help

This repository contains the source code for the Check-Ins Online Help. The online help is a static site generated using [Jekyll](https://jekyllrb.com/).

## Running the Application

### Prerequisites

- [Ruby](https://www.ruby-lang.org/en/documentation/installation/) (version 3.0.0 or later)
- [Bundler](https://bundler.io/)
- [Jekyll](https://jekyllrb.com/docs/installation/)
- [rbenv](https://github.com/rbenv/rbenv) (optional, for managing Ruby versions)

### Building

1. Clone the Check-Ins repository:

   ```shell
   git clone git@github.com:objectcomputing/check-ins.git
   ```

2. Change to the `docs` directory:

   ```shell
   cd check-ins/docs
   ```

   The `docs` directory contains the Jekyll site source code and a file called `.ruby-version` that specifies the expected Ruby version.

3. Install the required gems:

   ```shell
   gem install jekyll bundler
   ```

4. Install the gems specified in the `Gemfile`:

   ```shell
   bundle install
   ```

5. Build the site and make it available on the local server:

   ```shell
   bundle exec jekyll serve
   ```

6. Open a browser and navigate to `http://localhost:4000/`.
