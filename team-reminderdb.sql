-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost:8889
-- Généré le : mar. 25 avr. 2023 à 09:13
-- Version du serveur : 5.7.39
-- Version de PHP : 7.4.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `team-reminderdb`
--

-- --------------------------------------------------------

--
-- Structure de la table `etattype`
--

CREATE TABLE `etattype` (
  `id` tinyint(4) NOT NULL,
  `type` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Déchargement des données de la table `etattype`
--

INSERT INTO `etattype` (`id`, `type`) VALUES
(1, 'A faire'),
(2, 'En cours'),
(3, 'Fait'),
(4, 'reporté');

-- --------------------------------------------------------

--
-- Structure de la table `reccurencetype`
--

CREATE TABLE `reccurencetype` (
  `id` tinyint(4) NOT NULL,
  `type` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Déchargement des données de la table `reccurencetype`
--

INSERT INTO `reccurencetype` (`id`, `type`) VALUES
(1, 'Unique'),
(2, 'Quotidien'),
(3, 'Hebdomadaire'),
(4, 'Mensuel'),
(5, 'Annuel');

-- --------------------------------------------------------

--
-- Structure de la table `reminder`
--

CREATE TABLE `reminder` (
  `id` tinyint(4) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `lastScheldule` date DEFAULT NULL,
  `nextScheldule` date NOT NULL,
  `priority` tinyint(1) NOT NULL,
  `idReccurence` tinyint(4) NOT NULL,
  `idTaskType` tinyint(4) NOT NULL,
  `idReminderType` tinyint(4) NOT NULL,
  `idTeam` tinyint(4) DEFAULT NULL,
  `idetat` tinyint(4) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `remindertype`
--

CREATE TABLE `remindertype` (
  `id` tinyint(4) NOT NULL,
  `type` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Déchargement des données de la table `remindertype`
--

INSERT INTO `remindertype` (`id`, `type`) VALUES
(2, 'Ne pas recevoir de mail');

-- --------------------------------------------------------

--
-- Structure de la table `tasktype`
--

CREATE TABLE `tasktype` (
  `id` tinyint(4) NOT NULL,
  `type` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Déchargement des données de la table `tasktype`
--

INSERT INTO `tasktype` (`id`, `type`) VALUES
(1, 'Réunion'),
(2, 'Maintenance'),
(3, 'Convocation');

-- --------------------------------------------------------

--
-- Structure de la table `team`
--

CREATE TABLE `team` (
  `id` tinyint(4) NOT NULL,
  `name` varchar(50) NOT NULL,
  `codeAcces` varchar(5) NOT NULL,
  `email` varchar(50) NOT NULL,
  `super_admin` tinyint(1) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Déchargement des données de la table `team`
--

INSERT INTO `team` (`id`, `name`, `codeAcces`, `email`, `super_admin`) VALUES
(2, 'Administrator', '59875', 'email.test@gmail.com', 1);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `etattype`
--
ALTER TABLE `etattype`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `reccurencetype`
--
ALTER TABLE `reccurencetype`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `reminder`
--
ALTER TABLE `reminder`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idReccurence` (`idReccurence`),
  ADD KEY `idTaskType` (`idTaskType`),
  ADD KEY `idReminderTask` (`idReminderType`),
  ADD KEY `idTeam` (`idTeam`),
  ADD KEY `idetat` (`idetat`);

--
-- Index pour la table `remindertype`
--
ALTER TABLE `remindertype`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `tasktype`
--
ALTER TABLE `tasktype`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `team`
--
ALTER TABLE `team`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `etattype`
--
ALTER TABLE `etattype`
  MODIFY `id` tinyint(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `reccurencetype`
--
ALTER TABLE `reccurencetype`
  MODIFY `id` tinyint(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT pour la table `reminder`
--
ALTER TABLE `reminder`
  MODIFY `id` tinyint(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=46;

--
-- AUTO_INCREMENT pour la table `remindertype`
--
ALTER TABLE `remindertype`
  MODIFY `id` tinyint(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `tasktype`
--
ALTER TABLE `tasktype`
  MODIFY `id` tinyint(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `team`
--
ALTER TABLE `team`
  MODIFY `id` tinyint(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
