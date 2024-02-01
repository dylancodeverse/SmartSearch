create table aggregation(
    idaggregation serial primary key ,
    mots text unique ,
    operation text
);

insert into aggregation values
(default,'somme','sum(%)'),
(default,'moyenne','avg(%)'),
(default,'minimum','min(%)'),
(default,'maximum','max(%)');
(default,'rapport qualite prix','qualite/prix')


create table whereregex(
    idwhereregex serial primary key ,
    regex text unique,
    operation text ,
    nmatcher integer ,
    type text
);

insert into whereregex values
(default,'entre INT et INT','INT0<=column and column<INT1 ',2 ,'INT'),
(default,'inferieur INT','column<INT0 ',1 ,'INT'),
(default,'superieur INT','INT0<column ',1 ,'INT');


create table sort(
    idsort serial primary key ,
    mots text unique,
    operation text
);

insert into sort values
(default,'meilleur' ,'order by % desc');
(default,'m prix' ,'order by % asc');
(default,'pire' ,'order by % asc');



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