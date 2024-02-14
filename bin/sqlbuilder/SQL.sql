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

insert into sort values (
    default ,'pire' ,'order by % asc'
);

-- insert into sort values
-- (default,'pire' ,'order by % desc');




create table categorie(
    idcategorie varchar(250) primary key,
    categorie varchar(250) unique
);

insert into categorie values ('vetement', 'vetement');
insert into categorie values ('fruits', 'fruits');


create table produits(
    produitsid serial primary key ,
    nom varchar(250) unique,
    prix double precision default(0),
    categorie varchar(250),
    qualite integer,
    foreign key(categorie) references categorie(idcategorie)

);

insert into produits(nom,prix,categorie,qualite) values('debardeur',24,'vetement',10);
insert into produits(nom,prix,categorie,qualite) values('tshirt',28,'vetement',14);
insert into produits(nom,prix,categorie,qualite) values('short',16,'vetement',8);
insert into produits(nom,prix,categorie,qualite) values('pantalon',39,'vetement',20);


insert into produits(nom,prix,categorie,qualite) values('ananas',40,'fruits',7);
insert into produits(nom,prix,categorie,qualite) values('poire',32,'fruits',5);
insert into produits(nom,prix,categorie,qualite) values('pomme',20,'fruits',9);
insert into produits(nom,prix,categorie,qualite) values('cerise',10,'fruits',17);


create table ordre(
    nomcolomne text ,
    ordre text   ,
    defintion text
);

insert into ordre values(
    'prix',
    'asc',
    'meilleur'
);

insert into ordre values(
    'prix',
    'desc',
    'pire'
);

insert into ordre values(
    'sum(prix)',
    'asc',
    'meilleur'
);

insert into ordre values(
    'sum(prix)',
    'desc',
    'pire'
);