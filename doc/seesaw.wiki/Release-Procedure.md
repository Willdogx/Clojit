I'm kind of dumb, so for my sanity, here's how I do a release. See also https://github.com/nvie/gitflow

```sh
# Run tests
./autotest.sh

# Start release
git flow release start 1.0.2

# Bump version number from 1.0.2-SNAPSHOT to 1.0.2
vim project.clj  
git commit -a -m "Bumped version for 1.0.2 release"
git flow release finish 1.0.2

# Switch to tagged release, build and push to clojars
git checkout 1.0.2
rm lib/*.jar
lein clean
lein push

# Back to develop
git checkout develop

# Bump version from 1.0.2 to 1.0.3-SNAPSHOT
vim project.clj
git commit -a -m "Begin 1.0.3 dev"

# Push everything back to github
git push
git push --tags

# Update docs
lein autodoc
cd autodoc
git add -A
git commit -m "Doc update"
git push origin gh-pages

```