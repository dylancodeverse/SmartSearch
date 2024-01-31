create table operation(
    idoperation serial primary key ,
    mots text ,
    contexte text,
    operation text
);

create table regrouprement(
    idregroupement serial primary key ,
    mots text ,
    operation text 
);

create table aggregation(
    idaggregation serial primary key ,
    mots text ,
    operation text
);
-- sum(%) ,avg(%) , min(%),max(%)

insert into regrouprement  values (default ,'rapport' ,'/');


create table categorie(
    idcategorie varchar(250) primary key,
    categorie varchar(250) unique
);

create table produits(
    produitsid serial primary key ,
    nom varchar(250) unique,
    prix double precision default(0),
    categorie varchar(250),
    qualite integer,
     foreign key(categorie) references categorie(idcategorie)

);


insert into categorie values('boisson','boisson');

insert into operation values(default, 'meilleur' ,'client' ,'order by % asc');


insert into produits values (default ,'coca',534,'boisson',9);
insert into produits values (default ,'fanta' , 100 , 'boisson', 200);