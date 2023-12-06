create table userData(
    id serial primary key ,
    email varchar,
    password varchar

);
create table fileData(
    id serial primary key,
    filename varchar,
    userData_email varchar,
    FOREIGN KEY (userData_email) REFERENCES userdata(email),
    date timestamp(6),
    file_data oid,
    size bigint
)