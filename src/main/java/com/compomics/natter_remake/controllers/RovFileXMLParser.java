package com.compomics.natter_remake.controllers;

import com.compomics.natter_remake.model.ChargeState;
import com.compomics.natter_remake.model.HitRatio;
import com.compomics.natter_remake.model.Intensity;
import com.compomics.natter_remake.model.IntensityList;
import com.compomics.natter_remake.model.Modification;
import com.compomics.natter_remake.model.Peptide;
import com.compomics.natter_remake.model.PeptideGroup;
import com.compomics.natter_remake.model.PeptideMatch;
import com.compomics.natter_remake.model.PeptidePartner;
import com.compomics.natter_remake.model.Protein;
import com.compomics.natter_remake.model.Ratio;
import com.compomics.natter_remake.model.RawFile;
import com.compomics.natter_remake.model.RovFileData;
import com.compomics.natter_remake.model.Scan;
import com.compomics.natter_remake.model.ScanRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Davy
 */
public class RovFileXMLParser {

    //TODO this can be split up in classes for each big block in the constructor to help with readability and future iterations
    
    /**
     * {@docRoot all the methods that take a {@code XMLEventReader} work on the first block of what they are meant to parse. They move the {@code XMLEventReader} pointer to just past the block of xml they parsed
     * in some methods assumptions are made about the location of the pointer and if a line has been read or not
     * }
     */
    private XMLEvent rovFileLine;
    private RovFileData data;
    private Iterator<Attribute> XMLAttributes;
    private Map<Integer, Peptide> queryNumberToPeptide = new HashMap<Integer, Peptide>(500);
    private Map<Integer, PeptideMatch> peptideMatchIdToPeptideMatch = new HashMap<Integer, PeptideMatch>(500);
    private Map<Integer, PeptideGroup> peptideHitIdToPeptideGroup = new HashMap<Integer, PeptideGroup>(500);
    private Map<Integer, Modification> modificationsInFile = new HashMap<Integer, Modification>();

    /**
     * parsing constructor, parses the distiller xml file
     *
     * @param rovFileXMLReader the distiller xml event reader to parse
     * @throws XMLStreamException
     */
    public RovFileXMLParser(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        data = new RovFileData();
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("counters")) {
                    parseCounters(rovFileLine);
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("rawFile")) {
                    data.addRawFile(parseRawFile(rovFileXMLReader));
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("header")) {
                    parseHeader(rovFileXMLReader);
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("quantitation") && rovFileLine.asStartElement().getName().getPrefix().equalsIgnoreCase("mqm")) {
                    parseModifications(rovFileXMLReader);
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("peptideGrouping")) {
                    data.setPeptideGroups(parsePeptideGroups(rovFileXMLReader));
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("peptideMatch")) {
                    data.addPeptideMatch(parsePeptideMatch(rovFileXMLReader));
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("proteinhit")) {
                    data.addProteinHit(parseProteinHit(rovFileXMLReader));
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("peptide")) {
                    data.addPeptideQuery(parsePeptide(rovFileXMLReader));
                }
            }
        }
    }

    /**
     * parse a peptide xml block
     *
     * @param rovFileXMLReader the distiller reader
     * @return a distiller {@code Peptide}
     * @throws XMLStreamException
     */
    private Peptide parsePeptide(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        Peptide peptide = new Peptide();
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                XMLAttributes = rovFileLine.asStartElement().getAttributes();
                while (XMLAttributes.hasNext()) {
                    Attribute attribute = XMLAttributes.next();
                    if (attribute.getName().getLocalPart().equalsIgnoreCase("component")) {
                        peptide.setComposition(attribute.getValue());
                    } else if (attribute.getName().getLocalPart().equalsIgnoreCase("peptideStr")) {
                        peptide.setSequence(null);
                    } else if (attribute.getName().getLocalPart().equalsIgnoreCase("varModsStr")) {
                        peptide.setModifiedNumericalSequence(null);
                    } else if (attribute.getName().getLocalPart().equalsIgnoreCase("query")) {
                        peptide.setPeptideNumber(Integer.parseInt(attribute.getValue()));
                    }
                }
            } else if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("peptide")) {
                    queryNumberToPeptide.get(peptide.getPeptideNumber());
                    break;
                }
            }
        }
        return peptide;
    }

    /**
     * parse the headers of a distiller xml file
     *
     * @param rovFileXMLReader
     * @throws XMLStreamException
     */
    private void parseHeader(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                XMLAttributes = rovFileLine.asStartElement().getAttributes();
                String val = null;
                //find way to guarantee order of XMLAttributes                
                while (XMLAttributes.hasNext()) {
                    Attribute attribute = XMLAttributes.next();
                    if (attribute.getName().getLocalPart().equalsIgnoreCase("val")) {
                        val = attribute.getValue();
                    } else if (attribute.getValue().equalsIgnoreCase("CLE")) {
                        if (val != null && !val.isEmpty()) {
                            data.getHeader().setProteaseUsed(val);
                        } else if (XMLAttributes.hasNext()) {
                            data.getHeader().setProteaseUsed(attribute.getValue());
                        }
                    } else if (attribute.getValue().equalsIgnoreCase("DISTILLERVERSION")) {
                        if (val != null && !val.isEmpty()) {
                            data.getHeader().setDistillerVersion(val);
                        } else if (XMLAttributes.hasNext()) {
                            data.getHeader().setDistillerVersion(XMLAttributes.next().getValue());
                        }
                    } else if (attribute.getValue().equalsIgnoreCase("QUANTITATION")) {
                        if (val != null && !val.isEmpty()) {
                            data.getHeader().setQuantitationMethod(val);
                        } else if (XMLAttributes.hasNext()) {
                            data.getHeader().setQuantitationMethod(XMLAttributes.next().getValue());
                        }
                    } else if (attribute.getValue().equalsIgnoreCase("IONSCORECUTOFF")) {
                        if (val != null && !val.isEmpty()) {
                            data.getHeader().setCutOff(Integer.parseInt(val));
                        } else if (XMLAttributes.hasNext()) {
                            data.getHeader().setCutOff(Integer.parseInt(XMLAttributes.next().getValue()));
                        }
                        //todo find experiment with multiple mods
                    } else if (attribute.getValue().equalsIgnoreCase("MODS")) {
                        if (val != null && !val.isEmpty()) {
                            data.getHeader().addMod(val);
                        } else if (XMLAttributes.hasNext()) {
                            data.getHeader().addMod(XMLAttributes.next().getValue());
                        }
                    } else if (attribute.getValue().equalsIgnoreCase("FILENAME")) {
                        if (val != null && !val.isEmpty()) {
                            data.setFileName(val);
                        } else if (XMLAttributes.hasNext()) {
                            data.setFileName(XMLAttributes.next().getValue());
                        }
                    }
                }
            }
            if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("header")) {
                    break;
                }
            }
        }
    }

    /**
     * get the numbers of found instances from the header
     *
     * @param rovFileLine the line containing the counts
     * @throws XMLStreamException
     */
    private void parseCounters(XMLEvent rovFileLine) throws XMLStreamException {
        XMLAttributes = rovFileLine.asStartElement().getAttributes();
        while (XMLAttributes.hasNext()) {
            Attribute attribute = XMLAttributes.next();
            if (attribute.getName().getLocalPart().equalsIgnoreCase("peptideCount")) {
                data.getHeader().setFoundPeptides(Integer.parseInt(attribute.getValue()));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("peptideMatchCount")) {
                data.getHeader().setMatchedPeptides(Integer.parseInt(attribute.getValue()));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("proteinHitCount")) {
                data.getHeader().setMatchedProteins(Integer.parseInt(attribute.getValue()));
            }
        }
    }

    /**
     * parses the peptide grouping block
     *
     * @param rovFileXMLReader
     * @return a {@code List} of {@code PeptideGroup}s from the distiller xml
     * file
     * @throws XMLstreamException
     */
    private List<PeptideGroup> parsePeptideGroups(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        List<PeptideGroup> peptideGroups = new ArrayList<PeptideGroup>();
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("hit")) {
                    XMLAttributes = rovFileLine.asStartElement().getAttributes();
                    PeptideGroup peptideGroup = new PeptideGroup();
                    peptideGroup.setGroupNumber(Integer.parseInt(XMLAttributes.next().getValue()));
                    peptideGroup.addPeptides(parsePeptideGroupPeptides(rovFileXMLReader));
                    peptideHitIdToPeptideGroup.put(peptideGroup.getGroupNumber(), peptideGroup);
                    peptideGroups.add(peptideGroup);
                }
            } else if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("PeptideGrouping")) {
                    break;
                }
            }
        }
        return peptideGroups;
    }

    /**
     * parses the peptides in a peptide group block
     *
     * @param rovFileXMLReader the xml reader for the distiller xml file
     * @return a {@code List} of {@code Peptide}s parsed from the peptidegroup
     * block
     * @throws XMLStreamException
     */
    private List<Peptide> parsePeptideGroupPeptides(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        List<Peptide> parsedPeptides = new ArrayList<Peptide>(20);
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                Peptide peptide = new Peptide();
                XMLAttributes = rovFileLine.asStartElement().getAttributes();
                while (XMLAttributes.hasNext()) {
                    Attribute attribute = XMLAttributes.next();
                    if (attribute.getName().getLocalPart().equalsIgnoreCase("q")) {
                        peptide.setPeptideNumber(Integer.parseInt(attribute.getValue()));
                    } else if (attribute.getName().getLocalPart().equalsIgnoreCase("pepStr")) {
                        peptide.setSequence(attribute.getValue());
                    } else if (attribute.getName().getLocalPart().equalsIgnoreCase("comp")) {
                        peptide.setComposition(attribute.getValue());
                    } else if (attribute.getName().getLocalPart().equalsIgnoreCase("status")) {
                        if (attribute.getValue().equalsIgnoreCase("OK")) {
                            peptide.setValid(true);
                            parsedPeptides.add(peptide);
                        }
                    } else if (attribute.getName().getLocalPart().equalsIgnoreCase("varMods")) {
                        peptide.setModifiedNumericalSequence(attribute.getValue());
                    }
                }
                if (peptide.isValid()) {
                    peptide.setModifiedSequence(peptide.getModifiedNumericalSequence(), modificationsInFile);
                    queryNumberToPeptide.put(peptide.getPeptideNumber(), peptide);
                }

            } else if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("hit")) {
                    break;
                }
            }
        }
        return parsedPeptides;
    }

    /**
     * reads a {@code PeptideMatch} block from a distiller file xml
     *
     * @param rovFileXMLReader the distiller xml reader to read from
     * @return a {@code PeptideMatch}
     * @throws XMLStreamException
     */
    private PeptideMatch parsePeptideMatch(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        PeptideMatch peptideMatch = new PeptideMatch();
        parsePeptideMatchHeader(peptideMatch);
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("partner")) {
                    peptideMatch.addPartner(parsePeptidePartner(rovFileXMLReader));
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("chargeStateData")) {
                    peptideMatch.addChargeStateData(parseChargestateForPeptideMatch(rovFileXMLReader));
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("originalRatio")) {
                    peptideMatch.addOriginalRatio(parseAndAddDataToRatio(rovFileLine.asStartElement().getAttributes(), new Ratio()));
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("hitRatios")) {
                    peptideMatch.addHitRatio(parseRatioForPartners(rovFileXMLReader));
                }
            } else if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("peptideMatch")) {
                    peptideMatchIdToPeptideMatch.put(peptideMatch.getMatchId(), peptideMatch);
                    break;
                }
            }
        }
        return peptideMatch;
    }

    /**
     * parses and adds the data from the header to the {@code PeptideMatch}
     *
     * @param peptideMatch the {@code PeptideMatch} to add the header to
     * @throws XMLStreamException
     */
    private void parsePeptideMatchHeader(PeptideMatch peptideMatch) throws XMLStreamException {
        XMLAttributes = rovFileLine.asStartElement().getAttributes();
        while (XMLAttributes.hasNext()) {
            Attribute attribute = XMLAttributes.next();
            if (attribute.getName().getLocalPart().equalsIgnoreCase("id")) {
                peptideMatch.setMatchId(Integer.parseInt(attribute.getValue()));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("chargeState")) {
                peptideMatch.setChargeState(Integer.parseInt(attribute.getValue()));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("displayIntensity")) {
                peptideMatch.setIntensity(Double.parseDouble(attribute.getValue()));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("pepStr")) {
                peptideMatch.setPeptideSequence(attribute.getValue());
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("varModsF")) {
                peptideMatch.setMods(attribute.getValue());
            }
        }
    }

    /**
     * puts all the intensities from an intensity xml block
     *
     * @param rovFileXMLReader the distiller xml reader
     * @param intensitiesOfPartner the {@code IntensityList} to fill
     * @throws XMLStreamException
     */
    private void fillIntensityListForPartner(XMLEventReader rovFileXMLReader, IntensityList intensitiesOfPartner) throws XMLStreamException {
        parseXICForPartner(rovFileLine, intensitiesOfPartner);
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                Intensity intensity = new Intensity();
                XMLAttributes = rovFileLine.asStartElement().getAttributes();
                while (XMLAttributes.hasNext()) {
                    Attribute XMLAttribute = XMLAttributes.next();
                    if (XMLAttribute.getName().getLocalPart().equalsIgnoreCase("v")) {
                        intensity.setValue(Double.parseDouble((XMLAttribute.getValue())));
                    } else if (XMLAttribute.getName().getLocalPart().equalsIgnoreCase("scanid")) {
                        intensity.setScanid(Integer.parseInt(XMLAttribute.getValue()));
                    } else if (XMLAttribute.getName().getLocalPart().equalsIgnoreCase("rt")) {
                        intensity.setRetentionTime(Double.parseDouble(XMLAttribute.getValue()));
                    }
                }
                intensitiesOfPartner.add(intensity);
            } else if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("xic")) {
                    break;
                }
            }
        }
    }

    /**
     * parses a {@code PeptidePartner} block from a distiller xml file reader
     *
     * @param rovFileXMLReader the distiller xml file to read from
     * @return a {@code PeptidePartner}
     * @throws XMLStreamException
     */
    private PeptidePartner parsePeptidePartner(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        PeptidePartner partner = new PeptidePartner();
        XMLAttributes = rovFileLine.asStartElement().getAttributes();
        while (XMLAttributes.hasNext()) {
            Attribute attribute = XMLAttributes.next();
            if (attribute.getName().getLocalPart().equalsIgnoreCase("partnerIdentified")) {
                partner.setPartnerFound(attribute.getValue().equalsIgnoreCase("true"));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("component")) {
                partner.setComponent(attribute.getValue());
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("peptideString")) {
                partner.setPeptideSequence(attribute.getValue());
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("mOverZ")) {
                partner.setMassOverCharge(Double.parseDouble(attribute.getValue()));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("labelFreeVariableModifications")) {
                //partner.setModificationsOnPeptide(attribute.getValue());
            }
        }
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("xic")) {
                    fillIntensityListForPartner(rovFileXMLReader, partner.getIntensitiesForPartner());
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("matches")) {
                    parseMatchesForPartner(partner);
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("range")) {
                    partner.addRange(parseRangesForPartner());
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("absoluteValue")) {
                    partner.setAbsoluteRatio(parseAndAddDataToRatio(rovFileLine.asStartElement().getAttributes(), new Ratio()));
                }
            } else if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("partner")) {
                    break;
                }
            }
        }
        return partner;
    }

    /**
     * links {@code PeptideMatch}es that are connected to the {@code PeptidePartner} and adds them
     * @param peptidePartner the {@code PeptidePartner} to parse the {@code PeptideMatch}es for that are connected to it
     * @throws XMLStreamException
     */
    private void parseMatchesForPartner(PeptidePartner peptidePartner) throws XMLStreamException {
        XMLAttributes = rovFileLine.asStartElement().getAttributes();
        while (XMLAttributes.hasNext()) {
            Attribute attribute = XMLAttributes.next();
            if (attribute.getName().getLocalPart().equalsIgnoreCase("query")) {
                peptidePartner.addPeptidelinkToPartner(queryNumberToPeptide.get(Integer.parseInt(attribute.getValue())));
            } //else if (xmlAttribute.getName().getLocalPart().equalsIgnoreCase("rank")) {
            //dunno if useful
            // }
        }
    }

    /**
     * parses a {@code ChargeState} block from a distiller xml file reader
     *
     * @param rovFileXMLReader the reader to parse from
     * @return a {@code ChargeState}
     * @throws XMLStreamException
     */
    private ChargeState parseChargestateForPeptideMatch(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        ChargeState chargeState = new ChargeState();
        XMLAttributes = rovFileLine.asStartElement().getAttributes();
        while (XMLAttributes.hasNext()) {
            Attribute xmlAttribute = XMLAttributes.next();
            if (xmlAttribute.getName().getLocalPart().equalsIgnoreCase("bucketWidth")) {
                chargeState.setBucketWidth(Double.parseDouble(xmlAttribute.getValue()));
            } else if (xmlAttribute.getName().getLocalPart().equalsIgnoreCase("matchedRho")) {
                chargeState.setCorrelation(Double.parseDouble(xmlAttribute.getValue()));
            } else if (xmlAttribute.getName().getLocalPart().equalsIgnoreCase("totalIntensity")) {
                chargeState.setTotalIntensity(Double.parseDouble(xmlAttribute.getValue()));
            }
        }
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("bucketarea")) {
                    Scan scan = new Scan();

                    XMLAttributes = rovFileLine.asStartElement().getAttributes();
                    while (XMLAttributes.hasNext()) {
                        Attribute attribute = XMLAttributes.next();
                        if (attribute.getName().getLocalPart().equalsIgnoreCase("scan")) {
                            scan.setScanNumber(Integer.parseInt(attribute.getValue()));
                        } else if (attribute.getName().getLocalPart().equalsIgnoreCase("area")) {
                            scan.setArea(Double.parseDouble(attribute.getValue()));
                        }
                    }
                    chargeState.addScan(scan);
                }
            } else if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("chargestatedata")) {
                    break;
                }
            }
        }
        return chargeState;
    }

    /**
     * parses a XML attribute iterator and fills up the given {@code Ratio}
     * object
     *
     * @param XMLAttributes the {@code Attribute Iterator} to parse
     * @param ratio the {@code Ratio} to fill up
     * @return the {@code Ratio} that was the arg
     */
    private Ratio parseAndAddDataToRatio(Iterator<Attribute> XMLAttributes, Ratio ratio) {
        while (XMLAttributes.hasNext()) {
            Attribute attribute = XMLAttributes.next();
            if (attribute.getName().getLocalPart().equalsIgnoreCase("ratio")) {
                if (!attribute.getValue().isEmpty()) {
                    ratio.setRatio((attribute.getValue()));
                }
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("valid")) {
                ratio.setValid(attribute.getValue().equalsIgnoreCase("true"));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("value")) {
                ratio.setValue(Double.parseDouble(attribute.getValue()));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("quality")) {
                ratio.setQuality(Double.parseDouble(attribute.getValue()));
            }
        }
        return ratio;
    }

    /**
     * parses a {@code HitRatio} block in a distiller xml file
     *
     * @param rovFileXMLReader the distiller xml file reader
     * @return a {@code HitRatio}
     * @throws XMLStreamException
     */
    private HitRatio parseRatioForPartners(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        HitRatio ratio = new HitRatio();
        XMLAttributes = rovFileLine.asStartElement().getAttributes();
        while (XMLAttributes.hasNext()) {
            Attribute attribute = XMLAttributes.next();
            if (attribute.getName().getLocalPart().equalsIgnoreCase("hitNumber")) {
                ratio.setHitNumber((Integer.parseInt(attribute.getValue())));
            }
        }
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                parseAndAddDataToRatio(rovFileLine.asStartElement().getAttributes(), ratio);
            } else if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("hitratios")) {
                    break;
                }
            }
        }
        return ratio;
    }

    /**
     * parses a {@code Protein} block from a distiller xml file
     *
     * @param rovFileXMLReader the distiller xml file reader to parse from
     * @return the {@code Protein}
     * @throws XMLStreamException
     */
    private Protein parseProteinHit(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        Protein protein = new Protein();
        if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("proteinhit")) {
            XMLAttributes = rovFileLine.asStartElement().getAttributes();
            while (XMLAttributes.hasNext()) {
                Attribute attribute = XMLAttributes.next();
                if (attribute.getName().getLocalPart().equalsIgnoreCase("accession")) {
                    protein.setAccession(attribute.getValue());
                } else if (attribute.getName().getLocalPart().equalsIgnoreCase("score")) {
                    protein.setScore(Integer.parseInt(attribute.getValue()));
                } else if (attribute.getName().getLocalPart().equalsIgnoreCase("mass")) {
                    protein.setMass(Integer.parseInt(attribute.getValue()));
                }
            }
        }

        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("proteinratio")) {
                    Ratio ratio = new Ratio();
                    XMLAttributes = rovFileLine.asStartElement().getAttributes();
                    while (XMLAttributes.hasNext()) {
                        Attribute attribute = XMLAttributes.next();
                        if (attribute.getName().getLocalPart().equalsIgnoreCase("rationame")) {
                            ratio.setRatio(attribute.getValue());
                        } else if (attribute.getName().getLocalPart().equalsIgnoreCase("ratio")) {
                            ratio.setValue(Double.parseDouble(attribute.getValue()));
                        } else if (attribute.getName().getLocalPart().equalsIgnoreCase("valid")) {
                            ratio.setValid(attribute.getValue().contentEquals("true"));
                        } //else if (attribute.getName().getLocalPart().equalsIgnoreCase("stdev")) {
                        //}
                    }
                    protein.setRatio(ratio);
                } else if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("reftoPeptideMatch")) {
                    rovFileLine = rovFileXMLReader.nextEvent();
                    protein.addLinkToPeptideMatch(peptideMatchIdToPeptideMatch.get(Integer.parseInt(rovFileLine.asCharacters().getData())));
                }
            } else if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("proteinhit")) {
                    break;
                }
            }
        }
        return protein;
    }

    /**
     * parses the XIC peaks from the XIC xml line in a distiller xml file
     *
     * @param rovFileLine the xml line to parse
     * @param intensitiesForPartnter the {@code IntensityList} to add data to
     * @throws XMLStreamException
     */
    private void parseXICForPartner(XMLEvent rovFileLine, IntensityList intensitiesForPartnter) throws XMLStreamException {
        XMLAttributes = rovFileLine.asStartElement().getAttributes();
        while (XMLAttributes.hasNext()) {
            Attribute attribute = XMLAttributes.next();
            if (attribute.getName().getLocalPart().equalsIgnoreCase("peakState")) {
                intensitiesForPartnter.setValid(attribute.getValue().equalsIgnoreCase("ok"));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("XICPeakStart")) {
                intensitiesForPartnter.setPeakStart(Integer.parseInt(attribute.getValue()));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("XICPeakEnd")) {
                intensitiesForPartnter.setPeakEnd(Integer.parseInt(attribute.getValue()));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("XICRegionStart")) {
                intensitiesForPartnter.setPeakRegionStart(Integer.parseInt(attribute.getValue()));
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase("XICRegionEnd")) {
                intensitiesForPartnter.setPeakRegionEnd(Integer.parseInt(attribute.getValue()));
            }
        }
    }

    /**
     * parses the scan range from a line in the distiller xml file
     *
     * @return the {@code ScanRange}
     * @throws XMLStreamException
     */
    private ScanRange parseRangesForPartner() throws XMLStreamException {
        XMLAttributes = rovFileLine.asStartElement().getAttributes();
        ScanRange scanRange = new ScanRange();
        while (XMLAttributes.hasNext()) {
            Attribute attribute = XMLAttributes.next();
            if (attribute.getName().getLocalPart().equalsIgnoreCase("rt")) {
                if (attribute.getValue().contains("-")) {
                    String[] retentionTimeRange = attribute.getValue().split("-");
                    scanRange.setRetentionTime((Double.parseDouble(retentionTimeRange[0]) + Double.parseDouble(retentionTimeRange[1])) / 2);
                } else {
                    scanRange.setRetentionTime(Double.parseDouble(attribute.getValue()));
                }
            } else if (attribute.getName().getLocalPart().equalsIgnoreCase(("scan"))) {
                if (attribute.getValue().contains("-")) {
                    String[] scanIdRange = attribute.getValue().split("-");
                    scanRange.setScan((Double.parseDouble(scanIdRange[0]) + Double.parseDouble(scanIdRange[1])) / 2);
                } else {
                    scanRange.setScan(Double.parseDouble(attribute.getValue()));
                }
            }
        }
        return scanRange;
    }

    RovFileData getRovFileData() {
        return data;
    }

    /**
     * parses the rawfile info from a distiller xml file
     *
     * @param rovFileXMLReader the distiller xml file reader
     * @return the {@code RawFile}
     * @throws XMLStreamException
     */
    private RawFile parseRawFile(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        RawFile rawFile = new RawFile();
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                XMLAttributes = rovFileLine.asStartElement().getAttributes();
                String val = null;
                while (XMLAttributes.hasNext()) {
                    Attribute attribute = XMLAttributes.next();
                    if (attribute.getName().getLocalPart().equalsIgnoreCase("val")) {
                        val = attribute.getValue();
                    } else if (attribute.getValue().equalsIgnoreCase("filename")) {
                        if (val != null && !val.isEmpty()) {
                            rawFile.setRawFileName(val);
                        } else if (XMLAttributes.hasNext()) {
                            attribute = XMLAttributes.next();
                            rawFile.setRawFileName(attribute.getValue());
                        }
                    }
                }
                break;
            }
        }
        return rawFile;
    }

    /**
     * parses the modifications recoreded in a distiller xml file
     *
     * @param rovFileXMLReader the distiller xml file reader to parse from
     * @throws XMLStreamException
     */
    private void parseModifications(XMLEventReader rovFileXMLReader) throws XMLStreamException {
        int modcounter = 0;
        modificationsInFile.put(modcounter, new Modification());
        while (rovFileXMLReader.hasNext()) {
            rovFileLine = rovFileXMLReader.nextEvent();
            if (rovFileLine.isStartElement()) {
                if (rovFileLine.asStartElement().getName().getLocalPart().equalsIgnoreCase("mod_file")) {
                    rovFileLine = rovFileXMLReader.nextEvent();
                    modcounter += 1;
                    modificationsInFile.put(modcounter, new Modification(rovFileLine.asCharacters().getData()));
                }
            } else if (rovFileLine.isEndElement()) {
                if (rovFileLine.asEndElement().getName().getLocalPart().equalsIgnoreCase("quantitation") && rovFileLine.asEndElement().getName().getPrefix().equalsIgnoreCase("mqm")) {
                    break;
                }
            }
        }
    }
}