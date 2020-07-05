Working with Swing's `JTable` is usually a nightmare. Seesaw tries to make it less painful for common use cases. First note that there are two parts to a Seesaw table, the table itself and an underlying model. For the most part this distinction is hidden, but it's good to keep it in mind.

Please see [table.clj](https://github.com/daveray/seesaw/blob/master/src/seesaw/table.clj) for full details.

## Create a table
So, let's create a table with some initial data:

```clj
(table 
  :model [
    :columns [{:key :name, :text "Name"} :likes] 
    :rows '[["Bobby" "Laura Palmer"]
           ["Agent Cooper" "Cherry Pie"]
           {:likes "Laura Palmer" :name "James"}
           {:name "Big Ed" :likes "Norma Jennings"}]])
```

This creates a two-column table with four rows. Note a couple things:

* Each column has a key. A column can be specified as just a key, or a key along with text to display in the table header
* Each row is either a vector of values, or a map of values indexed by column key
* Here, the `:model` property, given a vector constructs the table's model using `(seesaw.table/table-model)`. Any other table model could be used here as well.

## Interrogate a table
We can grab values out of a table using `(value-at)`. Assume that `t` is bound to the table created above:

```clj
user=> (value-at t 1)
{:name "Agent Cooper" :likes "Cherry Pie"}

user=> (value-at t [0 2])
({:name "Bobby" :likes "Laura Palmer"} {:name "James" :likes "Laura Palmer})
```
Given a single row index, a single record is returned. Given a sequence of indices, a sequence of records is returned.

## Changing a table
We can update the value in one or more table rows using `(update-at!)`:

```clj
user=> (update-at! t 1 {:likes "Cherry Pie and coffee"} 
              2 {:name "Doctor Jacoby"})

user=> (value-at t [1 2])
({:name "Agent Cooper" :likes "Cherry Pie and coffee"},
 {:name "Doctor Jacoby" :likes "Laura Palmer})
```

## Inserting rows
Insert a row using `(insert-at!)`

```clj
user=> (row-count t)
4
user=> (insert-at! t 0 ["Log Lady" "Log"])
user=> (row-count t)
5
user=> (value-at t 0)
{:name "Log Lady" :likes "Log"}
```

Note that `(insert-at!)` can take an arbitrary number of row/value pairs.

### Deleting rows
Delete a row user `(remove-at!)`

```clj
user=> (remove-at! t 0 1 2)
user=> (row-count t)
1
```
